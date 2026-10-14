package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Scholar",
    val age: Int = 19,
    val studyLevel: StudyLevel = StudyLevel.COLLEGE_UNDERGRAD,
    val dailyGoalMinutes: Int = 300,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val autoBlockShorts: Boolean = true,
    val emergencyPauseAvailable: Boolean = true
)
