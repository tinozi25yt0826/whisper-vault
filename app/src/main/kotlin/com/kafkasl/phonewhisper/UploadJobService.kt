package com.kafkasl.phonewhisper

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

/** Sends new dictations to the PC. Android starts it only while charging on Wi-Fi, so it costs nothing on the go. */
class UploadJobService : JobService() {

    companion object {
        private const val PERIODIC_ID = 1
        private const val NOW_ID = 2
        private val TIME = DateTimeFormatter.ofPattern("dd.MM. HH:mm")

        fun schedule(ctx: Context) {
            val job = JobInfo.Builder(PERIODIC_ID, ComponentName(ctx, UploadJobService::class.java))
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(60 * 60 * 1000L)
                .setPersisted(true)
                .build()
            ctx.getSystemService(JobScheduler::class.java).schedule(job)
        }

        /** "Transfer now" from the settings: needs Wi-Fi, not charging. */
        fun runNow(ctx: Context) {
            val job = JobInfo.Builder(NOW_ID, ComponentName(ctx, UploadJobService::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build()
            ctx.getSystemService(JobScheduler::class.java).schedule(job)
        }
    }

    override fun onStartJob(params: JobParameters): Boolean {
        thread {
            val prefs = getSharedPreferences("phonewhisper", MODE_PRIVATE)
            val address = prefs.getString("pc_address", "").orEmpty()
            val key = prefs.getString("pc_key", "").orEmpty()
            val status = try {
                if (address.isBlank() || key.isBlank()) "not set up"
                else Uploader(DictationStore.publicDir(), File(filesDir, "sent.txt"), address, key).sendAll().let {
                    when {
                        it.error != null -> "${it.sent} sent, error: ${it.error}"
                        it.sent == 0 -> "nothing new"
                        else -> "${it.sent} files sent"
                    }
                }
            } catch (e: Throwable) { "error: $e" }
            prefs.edit().putString("upload_status", LocalDateTime.now().format(TIME) + " · " + status).apply()
            jobFinished(params, false)
        }
        return true
    }

    override fun onStopJob(params: JobParameters) = true  // interrupted: let Android retry
}
