package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: ScheduleCategory,
    val targetMinutes: Int,
    val actualSeconds: Int,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val blockedAppsAttemptCount: Int = 0,
    val shortsBlockedCount: Int = 0,
    val isCompleted: Boolean = true
)
