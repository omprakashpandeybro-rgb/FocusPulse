package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FocusSessionForegroundService : Service() {

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var updateJob: Job? = null

    companion object {
        const val CHANNEL_ID = "focus_pulse_session_channel"
        const val NOTIFICATION_ID = 2001

        const val ACTION_START = "ACTION_START_FOCUS_SESSION"
        const val ACTION_STOP = "ACTION_STOP_FOCUS_SESSION"

        const val EXTRA_TITLE = "EXTRA_SESSION_TITLE"
        const val EXTRA_DURATION_SEC = "EXTRA_DURATION_SEC"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "Deep Focus"
                startForegroundWithNotification(title)
                startUpdatingNotification()
            }
            ACTION_STOP -> {
                stopUpdatingNotification()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startForegroundWithNotification(title: String) {
        val notification = buildNotification(title, FocusSessionManager.remainingSeconds.value)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun startUpdatingNotification() {
        updateJob?.cancel()
        updateJob = scope.launch {
            while (FocusSessionManager.isActive.value) {
                delay(1000)
                val remaining = FocusSessionManager.remainingSeconds.value
                val title = FocusSessionManager.sessionTitle.value
                val notification = buildNotification(title, remaining)
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    private fun stopUpdatingNotification() {
        updateJob?.cancel()
        updateJob = null
    }

    private fun buildNotification(title: String, remainingSec: Int): Notification {
        val minutes = remainingSec / 60
        val seconds = remainingSec % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val shortsCount = FocusSessionManager.shortsBlocked.value
        val appsCount = FocusSessionManager.blockedAttempts.value
        val subtitle = if (shortsCount > 0 || appsCount > 0) {
            "$timeFormatted remaining • $shortsCount Shorts shielded, $appsCount apps locked"
        } else {
            "$timeFormatted remaining • Distraction Shield is Active"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Focus Mode: $title")
            .setContentText(subtitle)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Focus Session Active",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live remaining time during deep work sessions"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopUpdatingNotification()
        super.onDestroy()
    }
}
