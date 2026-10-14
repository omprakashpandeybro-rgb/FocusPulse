package com.example.data.repository

import com.example.data.db.UserProfileDao
import com.example.data.model.StudyLevel
import com.example.data.model.ThemeMode
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class UserProfileRepository(private val profileDao: UserProfileDao) {
    val userProfile: Flow<UserProfile?> = profileDao.getUserProfile()

    suspend fun getProfileOnce(): UserProfile? = profileDao.getUserProfileOnce()

    suspend fun updateProfile(profile: UserProfile) = profileDao.updateProfile(profile)

    suspend fun updateAgeAndLevel(age: Int, level: StudyLevel) {
        val current = profileDao.getUserProfileOnce() ?: UserProfile()
        val dailyGoalMinutes = (level.targetStudyHoursPerDay * 60).toInt()
        profileDao.updateProfile(
            current.copy(
                age = age,
                studyLevel = level,
                dailyGoalMinutes = dailyGoalMinutes
            )
        )
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        val current = profileDao.getUserProfileOnce() ?: UserProfile()
        profileDao.updateProfile(current.copy(themeMode = mode))
    }
}
