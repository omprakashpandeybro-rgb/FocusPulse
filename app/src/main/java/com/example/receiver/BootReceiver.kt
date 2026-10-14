package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.alarm.ScheduleAlarmManager
import com.example.data.db.FocusPulseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = FocusPulseDatabase.getDatabase(context)
                    val items = db.scheduleDao().getAlarmEnabledItems()
                    items.forEach { item ->
                        ScheduleAlarmManager.scheduleItemAlarm(context, item)
                    }
                } catch (_: Exception) {}
            }
        }
    }
}
