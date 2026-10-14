package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(tableName = "schedule_items")
data class ScheduleItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: ScheduleCategory,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val daysOfWeek: String = "1,2,3,4,5,6,7", // 1=Mon .. 7=Sun
    val isAlarmEnabled: Boolean = true,
    val isFocusShieldEnabled: Boolean = true,
    val isCompleted: Boolean = false,
    val specificDate: String = "" // YYYY-MM-DD or empty for recurring
) {
    fun formatTimeRange(): String {
        return "${formatTime(startHour, startMinute)} - ${formatTime(endHour, endMinute)}"
    }

    fun durationMinutes(): Int {
        var start = startHour * 60 + startMinute
        var end = endHour * 60 + endMinute
        if (end < start) end += 24 * 60
        return end - start
    }

    companion object {
        fun formatTime(hour: Int, minute: Int): String {
            val period = if (hour < 12) "AM" else "PM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, period)
        }
    }
}
