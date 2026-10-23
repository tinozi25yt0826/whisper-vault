package com.kafkasl.phonewhisper

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class UploaderTest {

    @get:Rule val tmp = TemporaryFolder()

    private val key = "abcd-efgh-2345"
    private val received = mutableMapOf<String, ByteArray>()
    private var proveWrongly = false
    private var refuse = setOf<String>()
    private val server = MockWebServer()
    private lateinit var dir: File
    private lateinit var sentLog: File

    /** Stands in for empfang.py on the PC. */
    @Before fun startFakePc() {
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val url = request.requestUrl!!
                if (url.encodedPath == "/hallo") {
                    val nonce = url.queryParameter("n")!!
                    return MockResponse().setBody(if (proveWrongly) "nope" else Uploader.proof(key, nonce))
                }
                val name = url.encodedPath.removePrefix("/upload/")
                val ok = request.getHeader("Authorization") == "Bearer $key" && name !in refuse
                if (ok) received[name] = request.body.readByteArray()
                return MockResponse().setResponseCode(if (ok) 201 else 409)
            }
        }
        server.start()
        dir = tmp.newFolder("Sportchronik")
        sentLog = File(tmp.root, "sent.txt")
    }

    @After fun stop() = server.shutdown()

    private fun uploader() = Uploader(dir, sentLog, "${server.hostName}:${server.port}", key)
    private fun file(name: String, text: String = name) = File(dir, name).apply { writeText(text) }

    @Test fun `proof matches the python receiver`() {
        // python: empfang.proof('abcd-efgh-2345', '0123456789abcdef0123456789abcdef')
        assertEquals("3e6e98729c05362dd7771099eea10174365c15e5c8e951efbc0bd6537f9b779e",
            Uploader.proof(key, "0123456789abcdef0123456789abcdef"))
    }

    @Test fun `only finished app files are pending, oldest first`() {
        file("2026-10-23_090000_bbbb.txt"); file("2026-10-23_080000_aaaa.wav")
        file("2026-10-23_100000_cccc.wav.part"); file("notiz.txt")
        assertEquals(listOf("2026-10-23_080000_aaaa.wav", "2026-10-23_090000_bbbb.txt"), uploader().pending().map { it.name })
    }

    @Test fun `sends new files once`() {
        file("2026-10-23_080000_aaaa.wav", "audio"); file("2026-10-23_080000_aaaa.txt", "Heute 5 km")
        assertEquals(Uploader.Result(2, null), uploader().sendAll())
        assertEquals("Heute 5 km", String(received.getValue("2026-10-23_080000_aaaa.txt")))
        received.clear()
        assertEquals(Uploader.Result(0, null), uploader().sendAll())
        assertTrue(received.isEmpty())
    }

    @Test fun `nothing is sent to a device that cannot prove the key`() {
        proveWrongly = true
        file("2026-10-23_080000_aaaa.txt")
        val result = uploader().sendAll()
        assertEquals(0, result.sent)
        assertTrue(result.error!!.contains("not recognised"))
        assertTrue(received.isEmpty())
    }

    @Test fun `a refused file does not block the others and is retried later`() {
        refuse = setOf("2026-10-23_080000_aaaa.txt")
        file("2026-10-23_080000_aaaa.txt"); file("2026-10-23_090000_bbbb.txt")
        val result = uploader().sendAll()
        assertEquals(1, result.sent)
        assertNotNull(result.error)
        assertEquals(listOf("2026-10-23_080000_aaaa.txt"), uploader().pending().map { it.name })
    }
}
