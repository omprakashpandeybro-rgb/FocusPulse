package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.model.ScheduleItem
import com.example.receiver.ScheduleAlarmReceiver
import java.util.Calendar

object ScheduleAlarmManager {

    fun scheduleItemAlarm(context: Context, item: ScheduleItem) {
        if (!item.isAlarmEnabled) {
            cancelItemAlarm(context, item.id)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // Compute next occurrence
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, item.startHour)
            set(Calendar.MINUTE, item.startMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(context, ScheduleAlarmReceiver::class.java).apply {
            action = ScheduleAlarmReceiver.ACTION_SCHEDULE_ALARM
            putExtra(ScheduleAlarmReceiver.EXTRA_ITEM_ID, item.id)
            putExtra(ScheduleAlarmReceiver.EXTRA_TITLE, item.title)
            putExtra(ScheduleAlarmReceiver.EXTRA_CATEGORY, item.category.name)
            putExtra(ScheduleAlarmReceiver.EXTRA_DURATION_MINUTES, item.durationMinutes())
            putExtra(ScheduleAlarmReceiver.EXTRA_IS_FOCUS_SHIELD, item.isFocusShieldEnabled)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        target.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        target.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    target.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                target.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelItemAlarm(context: Context, itemId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ScheduleAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun snoozeAlarm(context: Context, itemId: Long, title: String, categoryName: String, durationMin: Int, shield: Boolean, snoozeMinutes: Int = 5) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerAt = System.currentTimeMillis() + snoozeMinutes * 60 * 1000L

        val intent = Intent(context, ScheduleAlarmReceiver::class.java).apply {
            action = ScheduleAlarmReceiver.ACTION_SCHEDULE_ALARM
            putExtra(ScheduleAlarmReceiver.EXTRA_ITEM_ID, itemId)
            putExtra(ScheduleAlarmReceiver.EXTRA_TITLE, "$title (Snoozed)")
            putExtra(ScheduleAlarmReceiver.EXTRA_CATEGORY, categoryName)
            putExtra(ScheduleAlarmReceiver.EXTRA_DURATION_MINUTES, durationMin)
            putExtra(ScheduleAlarmReceiver.EXTRA_IS_FOCUS_SHIELD, shield)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (itemId + 10000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } catch (_: Exception) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }
}
