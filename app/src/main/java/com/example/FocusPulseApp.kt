package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.example.data.db.FocusPulseDatabase
import com.example.data.repository.FocusRepository
import com.example.data.repository.ScheduleRepository
import com.example.data.repository.UserProfileRepository
import com.example.receiver.ScheduleAlarmReceiver
import com.example.service.FocusSessionForegroundService

class FocusPulseApp : Application() {

    lateinit var database: FocusPulseDatabase
        private set

    lateinit var scheduleRepository: ScheduleRepository
        private set

    lateinit var focusRepository: FocusRepository
        private set

    lateinit var userProfileRepository: UserProfileRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = FocusPulseDatabase.getDatabase(this)
        scheduleRepository = ScheduleRepository(database.scheduleDao())
        focusRepository = FocusRepository(
            database.focusSessionDao(),
            database.blockedAppDao(),
            database.blockEventDao()
        )
        userProfileRepository = UserProfileRepository(database.userProfileDao())

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            // Alarm Channel
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val alarmChannel = NotificationChannel(
                ScheduleAlarmReceiver.CHANNEL_ID,
                getString(R.string.notification_channel_alarm_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_alarm_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(alarmSound, audioAttributes)
            }

            // Focus Session Channel
            val sessionChannel = NotificationChannel(
                FocusSessionForegroundService.CHANNEL_ID,
                getString(R.string.notification_channel_focus_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_focus_desc)
                setShowBadge(false)
            }

            manager.createNotificationChannel(alarmChannel)
            manager.createNotificationChannel(sessionChannel)
        }
    }
}
