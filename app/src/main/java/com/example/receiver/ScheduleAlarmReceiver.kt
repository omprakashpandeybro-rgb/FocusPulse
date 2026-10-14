package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.ui.alarm.AlarmRingingActivity

class ScheduleAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SCHEDULE_ALARM = "com.example.focuspulse.ACTION_SCHEDULE_ALARM"
        const val EXTRA_ITEM_ID = "EXTRA_ITEM_ID"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
        const val EXTRA_DURATION_MINUTES = "EXTRA_DURATION_MINUTES"
        const val EXTRA_IS_FOCUS_SHIELD = "EXTRA_IS_FOCUS_SHIELD"

        const val CHANNEL_ID = "focus_pulse_alarm_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val itemId = intent.getLongExtra(EXTRA_ITEM_ID, 0L)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Scheduled Task"
        val categoryName = intent.getStringExtra(EXTRA_CATEGORY) ?: "WORK"
        val durationMinutes = intent.getIntExtra(EXTRA_DURATION_MINUTES, 30)
        val isFocusShield = intent.getBooleanExtra(EXTRA_IS_FOCUS_SHIELD, true)

        createAlarmChannel(context)

        // Launch full-screen alarm ringing activity
        val alarmIntent = Intent(context, AlarmRingingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            putExtra(EXTRA_ITEM_ID, itemId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_CATEGORY, categoryName)
            putExtra(EXTRA_DURATION_MINUTES, durationMinutes)
            putExtra(EXTRA_IS_FOCUS_SHIELD, isFocusShield)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            itemId.toInt(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Notification builder with high importance
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("⏰ Time for: $title")
            .setContentText("Scheduled block starting now ($durationMinutes min). Tap to focus.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 500))
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((1000 + itemId).toInt(), notification)

        // Directly launch the activity so user sees the ringing alarm immediately
        try {
            context.startActivity(alarmIntent)
        } catch (_: Exception) {}

        // Vibrate immediately
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 600, 200, 600, 200, 600), -1))
            } else {
                vibrator.vibrate(longArrayOf(0, 600, 200, 600, 200, 600), -1)
            }
        }
    }

    private fun createAlarmChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Schedule Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Loud alarm reminders for scheduled focus blocks"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(alarmSound, audioAttributes)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
