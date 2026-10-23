package com.kafkasl.phonewhisper

import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.io.RandomAccessFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Keeps every dictation as <name>.wav + <name>.txt, ready to be synced to the PC.
 * Files are written as *.part and renamed once complete, so a sync tool that
 * ignores *.part never picks up a half-written file.
 */
class DictationStore(val dir: File) {

    companion object {
        private val NAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss")
        private const val ALPHABET = "abcdefghijkmnpqrstuvwxyz23456789"
        private const val HEADER_SIZE = 44
    }

    /** Sorts chronologically; the random suffix keeps two dictations in the same second apart. */
    fun newName(now: LocalDateTime = LocalDateTime.now()): String =
        now.format(NAME_FORMAT) + "_" + (1..4).map { ALPHABET.random() }.joinToString("")

    /** Opens <name>.wav.part with a placeholder header; append raw 16 kHz mono PCM to the stream. */
    fun startWav(name: String): OutputStream {
        if (!dir.isDirectory && !dir.mkdirs()) throw IOException("Cannot create $dir")
        val out = File(dir, "$name.wav.part").outputStream().buffered()
        out.write(WavWriter.header(0))
        return out
    }

    /** Writes the real sizes into the header and publishes <name>.wav. Returns null (and deletes) when there is no audio. */
    fun finishWav(part: File): File? {
        val dataSize = part.length() - HEADER_SIZE
        if (dataSize <= 0) { part.delete(); return null }
        RandomAccessFile(part, "rw").use { it.write(WavWriter.header(dataSize.toInt())) }
        return publish(part)
    }

    fun saveText(name: String, text: String): File {
        val part = File(dir, "$name.txt.part")
        part.writeText(text)
        return publish(part)
    }

    /** Repairs recordings cut off by a crash or a killed app; returns how many were saved. */
    fun recover(): Int {
        val parts = dir.listFiles() ?: return 0
        parts.filter { it.name.endsWith(".txt.part") }.forEach { it.delete() }
        return parts.filter { it.name.endsWith(".wav.part") }.count { finishWav(it) != null }
    }

    private fun publish(part: File): File {
        val target = File(part.parentFile, part.name.removeSuffix(".part"))
        if (!part.renameTo(target)) throw IOException("Cannot rename $part")
        return target
    }
}
