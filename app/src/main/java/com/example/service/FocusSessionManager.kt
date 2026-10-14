package com.example.service

import android.content.Context
import android.content.Intent
import com.example.data.db.FocusPulseDatabase
import com.example.data.model.BlockEvent
import com.example.data.model.FocusSession
import com.example.data.model.ScheduleCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object FocusSessionManager {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _sessionTitle = MutableStateFlow("Deep Focus")
    val sessionTitle: StateFlow<String> = _sessionTitle.asStateFlow()

    private val _sessionCategory = MutableStateFlow(ScheduleCategory.STUDY)
    val sessionCategory: StateFlow<ScheduleCategory> = _sessionCategory.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(25 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(25 * 60)
    val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    private val _blockedAttempts = MutableStateFlow(0)
    val blockedAttempts: StateFlow<Int> = _blockedAttempts.asStateFlow()

    private val _shortsBlocked = MutableStateFlow(0)
    val shortsBlocked: StateFlow<Int> = _shortsBlocked.asStateFlow()

    private val _isAccessibilityConnected = MutableStateFlow(false)
    val isAccessibilityConnected: StateFlow<Boolean> = _isAccessibilityConnected.asStateFlow()

    private val _isShortsFilterEnabled = MutableStateFlow(true)
    val isShortsFilterEnabled: StateFlow<Boolean> = _isShortsFilterEnabled.asStateFlow()

    private var sessionStartTimeMillis: Long = 0L

    fun setAccessibilityConnected(connected: Boolean) {
        _isAccessibilityConnected.value = connected
    }

    fun setShortsFilterEnabled(enabled: Boolean) {
        _isShortsFilterEnabled.value = enabled
    }

    fun startSession(
        context: Context,
        title: String,
        category: ScheduleCategory,
        durationMinutes: Int
    ) {
        timerJob?.cancel()
        _sessionTitle.value = title
        _sessionCategory.value = category
        _remainingSeconds.value = durationMinutes * 60
        _totalDurationSeconds.value = durationMinutes * 60
        _blockedAttempts.value = 0
        _shortsBlocked.value = 0
        _isActive.value = true
        sessionStartTimeMillis = System.currentTimeMillis()

        // Start Foreground Service for persistence
        val intent = Intent(context, FocusSessionForegroundService::class.java).apply {
            action = FocusSessionForegroundService.ACTION_START
            putExtra(FocusSessionForegroundService.EXTRA_TITLE, title)
            putExtra(FocusSessionForegroundService.EXTRA_DURATION_SEC, durationMinutes * 60)
        }
        try {
            context.startForegroundService(intent)
        } catch (_: Exception) {
            // Foreground service may fallback
        }

        timerJob = scope.launch {
            while (_remainingSeconds.value > 0 && _isActive.value) {
                delay(1000)
                _remainingSeconds.value = (_remainingSeconds.value - 1).coerceAtLeast(0)
            }
            if (_remainingSeconds.value <= 0 && _isActive.value) {
                completeSession(context)
            }
        }
    }

    fun stopSession(context: Context) {
        if (!_isActive.value) return
        val elapsedSeconds = _totalDurationSeconds.value - _remainingSeconds.value
        val durationMinutes = _totalDurationSeconds.value / 60
        saveSessionToDb(context, elapsedSeconds, durationMinutes, false)

        _isActive.value = false
        timerJob?.cancel()

        val intent = Intent(context, FocusSessionForegroundService::class.java).apply {
            action = FocusSessionForegroundService.ACTION_STOP
        }
        try {
            context.startService(intent)
        } catch (_: Exception) {}
    }

    private fun completeSession(context: Context) {
        _isActive.value = false
        val durationMinutes = _totalDurationSeconds.value / 60
        saveSessionToDb(context, _totalDurationSeconds.value, durationMinutes, true)

        val intent = Intent(context, FocusSessionForegroundService::class.java).apply {
            action = FocusSessionForegroundService.ACTION_STOP
        }
        try {
            context.startService(intent)
        } catch (_: Exception) {}
    }

    fun grantEmergencyBreak(minutes: Int) {
        // Pauses or delays blocker for X minutes
        _remainingSeconds.value = (_remainingSeconds.value + minutes * 60)
    }

    fun recordBlockedAttempt(
        context: Context,
        packageName: String,
        appName: String,
        wasShorts: Boolean
    ) {
        _blockedAttempts.value = _blockedAttempts.value + 1
        if (wasShorts) {
            _shortsBlocked.value = _shortsBlocked.value + 1
        }

        CoroutineScope(Dispatchers.IO).launch {
            val db = FocusPulseDatabase.getDatabase(context)
            val reason = if (wasShorts) "YouTube Shorts intercepted (Study videos permitted)" else "$appName blocked during focus session"
            db.blockEventDao().recordEvent(
                BlockEvent(
                    timestamp = System.currentTimeMillis(),
                    packageName = packageName,
                    appName = appName,
                    reason = reason,
                    wasShorts = wasShorts
                )
            )
        }
    }

    private fun saveSessionToDb(
        context: Context,
        actualSeconds: Int,
        targetMinutes: Int,
        isCompleted: Boolean
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = FocusPulseDatabase.getDatabase(context)
            db.focusSessionDao().insertSession(
                FocusSession(
                    title = _sessionTitle.value,
                    category = _sessionCategory.value,
                    targetMinutes = targetMinutes,
                    actualSeconds = actualSeconds,
                    startTimeMillis = sessionStartTimeMillis,
                    endTimeMillis = System.currentTimeMillis(),
                    blockedAppsAttemptCount = _blockedAttempts.value,
                    shortsBlockedCount = _shortsBlocked.value,
                    isCompleted = isCompleted
                )
            )
        }
    }
}
