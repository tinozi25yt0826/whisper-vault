package com.kafkasl.phonewhisper

import android.app.ActivityManager
import android.app.ApplicationExitInfo
import android.content.Context
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Shows why the app stopped recently, so a vanished overlay button can be explained without adb. */
object Diagnostics {
    const val LAST_CRASH_KEY = "last_crash"
    private val TIME = DateTimeFormatter.ofPattern("dd.MM. HH:mm")

    fun exitReasonLabel(reason: Int): String = when (reason) {
        ApplicationExitInfo.REASON_CRASH -> "crash"
        ApplicationExitInfo.REASON_CRASH_NATIVE -> "crash (native)"
        ApplicationExitInfo.REASON_ANR -> "not responding"
        ApplicationExitInfo.REASON_LOW_MEMORY -> "killed: low memory"
        ApplicationExitInfo.REASON_EXCESSIVE_RESOURCE_USAGE -> "killed: too much resource use"
        ApplicationExitInfo.REASON_SIGNALED -> "killed by system"
        ApplicationExitInfo.REASON_USER_REQUESTED -> "stopped (e.g. app update)"
        ApplicationExitInfo.REASON_USER_STOPPED -> "force-stopped"
        ApplicationExitInfo.REASON_PERMISSION_CHANGE -> "permission changed"
        ApplicationExitInfo.REASON_EXIT_SELF -> "exited"
        else -> "other ($reason)"
    }

    fun describeCrash(e: Throwable): String =
        LocalDateTime.now().format(TIME) + "  " + e + "\n" + e.stackTrace.take(6).joinToString("\n") { "  at $it" }

    /** Newest first, one line each. */
    fun recentExits(ctx: Context, max: Int = 5): List<String> =
        ctx.getSystemService(ActivityManager::class.java)
            .getHistoricalProcessExitReasons(ctx.packageName, 0, max)
            .map {
                val time = Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).format(TIME)
                "$time  ${exitReasonLabel(it.reason)}" + (it.description?.let { d -> " – $d" } ?: "")
            }
}
