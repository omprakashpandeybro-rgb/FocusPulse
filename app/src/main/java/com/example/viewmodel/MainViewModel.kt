package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.FocusPulseApp
import com.example.alarm.ScheduleAlarmManager
import com.example.data.model.BlockedAppConfig
import com.example.data.model.ScheduleCategory
import com.example.data.model.ScheduleItem
import com.example.data.model.StudyLevel
import com.example.data.model.ThemeMode
import com.example.data.model.UserProfile
import com.example.data.schedule.AgeScheduleTemplates
import com.example.service.FocusSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FocusPulseApp
    private val scheduleRepo = app.scheduleRepository
    private val focusRepo = app.focusRepository
    private val profileRepo = app.userProfileRepository

    val userProfile: StateFlow<UserProfile?> = profileRepo.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val scheduleItems: StateFlow<List<ScheduleItem>> = scheduleRepo.allItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedApps: StateFlow<List<BlockedAppConfig>> = focusRepo.blockedApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentBlockEvents = focusRepo.recentBlockEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeFocusState = FocusSessionManager.isActive
    val remainingSeconds = FocusSessionManager.remainingSeconds
    val focusSessionTitle = FocusSessionManager.sessionTitle
    val focusCategory = FocusSessionManager.sessionCategory
    val focusBlockedCount = FocusSessionManager.blockedAttempts
    val focusShortsCount = FocusSessionManager.shortsBlocked
    val isAccessibilityConnected = FocusSessionManager.isAccessibilityConnected
    val isShortsFilterActive = FocusSessionManager.isShortsFilterEnabled

    private val startOfDayMillis: Long
        get() {
            val c = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return c.timeInMillis
        }

    val todayFocusSeconds = focusRepo.getTodayFocusSeconds(startOfDayMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todaySessionCount = focusRepo.getTodaySessionCount(startOfDayMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayBlockCount = focusRepo.getTodayBlockCount(startOfDayMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayShortsCount = focusRepo.getTodayShortsBlockCount(startOfDayMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearMessage() {
        _userMessage.value = null
    }

    fun applyAgeRecommendedSchedule(level: StudyLevel, age: Int) {
        viewModelScope.launch {
            val items = AgeScheduleTemplates.generateScheduleForLevel(level, age)
            scheduleRepo.replaceWithTemplate(items)
            profileRepo.updateAgeAndLevel(age, level)

            // Re-register alarms for new items
            val context = getApplication<Application>().applicationContext
            items.forEach { item ->
                if (item.isAlarmEnabled) {
                    ScheduleAlarmManager.scheduleItemAlarm(context, item)
                }
            }
            _userMessage.value = "Applied recommended schedule for ${level.title} (${level.ageRange})"
        }
    }

    fun addScheduleItem(
        title: String,
        description: String,
        category: ScheduleCategory,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        isAlarmEnabled: Boolean,
        isFocusShieldEnabled: Boolean
    ) {
        viewModelScope.launch {
            val item = ScheduleItem(
                title = title,
                description = description,
                category = category,
                startHour = startHour,
                startMinute = startMinute,
                endHour = endHour,
                endMinute = endMinute,
                isAlarmEnabled = isAlarmEnabled,
                isFocusShieldEnabled = isFocusShieldEnabled
            )
            val id = scheduleRepo.insertItem(item)
            if (isAlarmEnabled) {
                ScheduleAlarmManager.scheduleItemAlarm(
                    getApplication<Application>().applicationContext,
                    item.copy(id = id)
                )
            }
            _userMessage.value = "Scheduled '$title' at ${ScheduleItem.formatTime(startHour, startMinute)}"
        }
    }

    fun updateScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            scheduleRepo.updateItem(item)
            val context = getApplication<Application>().applicationContext
            if (item.isAlarmEnabled) {
                ScheduleAlarmManager.scheduleItemAlarm(context, item)
            } else {
                ScheduleAlarmManager.cancelItemAlarm(context, item.id)
            }
        }
    }

    fun deleteScheduleItem(item: ScheduleItem) {
        viewModelScope.launch {
            scheduleRepo.deleteItem(item)
            ScheduleAlarmManager.cancelItemAlarm(
                getApplication<Application>().applicationContext,
                item.id
            )
            _userMessage.value = "Removed scheduled block '${item.title}'"
        }
    }

    fun toggleItemCompleted(item: ScheduleItem) {
        viewModelScope.launch {
            scheduleRepo.toggleCompleted(item.id, !item.isCompleted)
        }
    }

    fun toggleItemAlarm(item: ScheduleItem, enabled: Boolean) {
        viewModelScope.launch {
            val updated = item.copy(isAlarmEnabled = enabled)
            scheduleRepo.updateItem(updated)
            val context = getApplication<Application>().applicationContext
            if (enabled) {
                ScheduleAlarmManager.scheduleItemAlarm(context, updated)
                _userMessage.value = "Alarm set for ${ScheduleItem.formatTime(item.startHour, item.startMinute)}"
            } else {
                ScheduleAlarmManager.cancelItemAlarm(context, item.id)
                _userMessage.value = "Alarm turned off for '${item.title}'"
            }
        }
    }

    fun testAlarmImmediately(item: ScheduleItem) {
        ScheduleAlarmManager.snoozeAlarm(
            context = getApplication<Application>().applicationContext,
            itemId = item.id,
            title = item.title,
            categoryName = item.category.name,
            durationMin = item.durationMinutes(),
            shield = item.isFocusShieldEnabled,
            snoozeMinutes = 0 // Immediate trigger
        )
        _userMessage.value = "Testing alarm for '${item.title}' in a few seconds..."
    }

    fun startFocusSession(
        title: String,
        category: ScheduleCategory,
        durationMinutes: Int
    ) {
        FocusSessionManager.startSession(
            context = getApplication<Application>().applicationContext,
            title = title,
            category = category,
            durationMinutes = durationMinutes
        )
        _userMessage.value = "Focus Mode armed: $durationMinutes min. Distractions locked!"
    }

    fun stopFocusSession() {
        FocusSessionManager.stopSession(getApplication<Application>().applicationContext)
        _userMessage.value = "Focus session ended and logged to analytics."
    }

    fun toggleBlockedApp(appConfig: BlockedAppConfig, isBlocked: Boolean) {
        viewModelScope.launch {
            focusRepo.updateAppBlockConfig(appConfig.copy(isBlocked = isBlocked))
        }
    }

    fun toggleShortsOnly(appConfig: BlockedAppConfig, shortsOnly: Boolean) {
        viewModelScope.launch {
            focusRepo.updateAppBlockConfig(appConfig.copy(blockShortsOnly = shortsOnly))
            FocusSessionManager.setShortsFilterEnabled(shortsOnly)
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            profileRepo.updateThemeMode(mode)
        }
    }

    fun updateProfile(age: Int, level: StudyLevel, goalMinutes: Int) {
        viewModelScope.launch {
            val current = profileRepo.getProfileOnce() ?: UserProfile()
            profileRepo.updateProfile(
                current.copy(
                    age = age,
                    studyLevel = level,
                    dailyGoalMinutes = goalMinutes
                )
            )
            _userMessage.value = "Profile updated: Age $age, ${level.title}"
        }
    }

    fun simulateDistractionShield(isShorts: Boolean) {
        val appName = if (isShorts) "YouTube Shorts" else "Instagram"
        FocusSessionManager.recordBlockedAttempt(
            context = getApplication<Application>().applicationContext,
            packageName = if (isShorts) "com.google.android.youtube" else "com.instagram.android",
            appName = appName,
            wasShorts = isShorts
        )
        _userMessage.value = if (isShorts) {
            "Simulated: YouTube Shorts intercepted! (Study lectures remain unblocked)"
        } else {
            "Simulated: $appName locked during focus session!"
        }
    }
}
