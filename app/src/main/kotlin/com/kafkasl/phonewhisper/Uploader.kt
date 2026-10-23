package com.kafkasl.phonewhisper

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Sends finished dictations to the PC receiver (empfang/empfang.py) and remembers what was sent.
 * The PC first has to prove it knows the key, so on a foreign Wi-Fi nothing leaks to a stranger's
 * device that happens to have the same address.
 */
class Uploader(
    private val dir: File,
    private val sentLog: File,
    address: String,
    private val key: String,
    private val client: OkHttpClient = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build(),
) {
    data class Result(val sent: Int, val error: String?)

    private class Refused(msg: String) : IOException(msg)

    companion object {
        private val NAME = Regex("""\d{4}-\d{2}-\d{2}_\d{6}_[a-z2-9]{4}\.(wav|txt)""")
        private val OCTET = "application/octet-stream".toMediaType()

        fun proof(key: String, nonce: String): String {
            val mac = Mac.getInstance("HmacSHA256").apply { init(SecretKeySpec(key.toByteArray(), "HmacSHA256")) }
            return mac.doFinal(nonce.toByteArray()).joinToString("") { "%02x".format(it) }
        }
    }

    // shortcut: plain HTTP inside the home network (protected by Wi-Fi encryption); add TLS if it ever leaves the LAN
    private val base = (if ("://" in address) address else "http://$address").trimEnd('/')

    /** Finished dictations not sent yet, oldest first (*.part files are still being written). */
    fun pending(): List<File> {
        val sent = if (sentLog.exists()) sentLog.readLines().toHashSet() else emptySet()
        return dir.listFiles().orEmpty().filter { NAME.matches(it.name) && it.name !in sent }.sortedBy { it.name }
    }

    /** A file the PC refuses is reported and retried next time; a lost connection stops the run. */
    fun sendAll(): Result {
        val files = pending()
        if (files.isEmpty()) return Result(0, null)
        try { verifyPc() } catch (e: IOException) { return Result(0, e.message) }
        var sent = 0
        var error: String? = null
        for (f in files) {
            try {
                send(f)
                sentLog.appendText(f.name + "\n")
                sent++
            } catch (e: Refused) {
                error = e.message
            } catch (e: IOException) {
                return Result(sent, e.message)
            }
        }
        return Result(sent, error)
    }

    private fun verifyPc() {
        val nonce = ByteArray(16).also { SecureRandom().nextBytes(it) }.joinToString("") { "%02x".format(it) }
        client.newCall(Request.Builder().url("$base/hallo?n=$nonce").build()).execute().use {
            if (!it.isSuccessful || it.body?.string()?.trim() != proof(key, nonce)) {
                throw IOException("PC not recognised (check address and key)")
            }
        }
    }

    private fun send(f: File) {
        val req = Request.Builder().url("$base/upload/${f.name}")
            .header("Authorization", "Bearer $key")
            .put(f.asRequestBody(OCTET))
            .build()
        client.newCall(req).execute().use {
            if (it.code != 200 && it.code != 201) throw Refused("${f.name}: ${it.code} ${it.body?.string()?.trim()}")
        }
    }
}
