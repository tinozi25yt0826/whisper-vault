package com.kafkasl.phonewhisper

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.LocalDateTime

class DictationStoreTest {

    @get:Rule val tmp = TemporaryFolder()

    private fun store() = DictationStore(File(tmp.root, "Sportchronik"))
    private fun readInt(b: ByteArray, off: Int) =
        (0..3).sumOf { (b[off + it].toInt() and 0xFF) shl (8 * it) }

    @Test fun `name sorts by time and has a random suffix`() {
        val name = store().newName(LocalDateTime.of(2026, 10, 23, 7, 42, 18))
        assertTrue(name, Regex("2026-10-23_074218_[a-z2-9]{4}").matches(name))
    }

    @Test fun `recording becomes a valid wav and no part file remains`() {
        val s = store()
        val pcm = ByteArray(3200) { it.toByte() }
        s.startWav("rec").use { it.write(pcm) }
        val wav = s.finishWav(File(s.dir, "rec.wav.part"))!!

        assertEquals("rec.wav", wav.name)
        val bytes = wav.readBytes()
        assertEquals(44 + pcm.size, bytes.size)
        assertEquals("RIFF", String(bytes.copyOfRange(0, 4)))
        assertEquals(pcm.size, readInt(bytes, 40))
        assertEquals(36 + pcm.size, readInt(bytes, 4))
        assertArrayEquals(pcm, bytes.copyOfRange(44, bytes.size))
        assertEquals(listOf("rec.wav"), s.dir.list()!!.toList())
    }

    @Test fun `transcript is saved next to the audio`() {
        val s = store()
        s.startWav("rec").close()
        val txt = s.saveText("rec", "Heute 5 km gelaufen, Knie ok.")
        assertEquals("Heute 5 km gelaufen, Knie ok.", txt.readText())
        assertFalse(File(s.dir, "rec.txt.part").exists())
    }

    @Test fun `recording without audio leaves no file`() {
        val s = store()
        s.startWav("empty").close()
        assertNull(s.finishWav(File(s.dir, "empty.wav.part")))
        assertEquals(0, s.dir.list()!!.size)
    }

    @Test fun `recover finishes cut-off recordings and drops broken leftovers`() {
        val s = store()
        s.startWav("crashed").use { it.write(ByteArray(1600)) }   // app killed before finishing
        File(s.dir, "stub.wav.part").writeBytes(ByteArray(10))     // killed while writing the header
        File(s.dir, "half.txt.part").writeText("unvoll")

        assertEquals(1, s.recover())
        assertEquals(setOf("crashed.wav"), s.dir.list()!!.toSet())
        assertEquals(1600, readInt(File(s.dir, "crashed.wav").readBytes(), 40))
    }

    @Test fun `recover without folder does nothing`() {
        assertEquals(0, store().recover())
    }
}
