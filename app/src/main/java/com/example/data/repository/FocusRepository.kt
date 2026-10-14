package com.example.data.repository

import com.example.data.db.BlockEventDao
import com.example.data.db.BlockedAppDao
import com.example.data.db.FocusSessionDao
import com.example.data.model.BlockEvent
import com.example.data.model.BlockedAppConfig
import com.example.data.model.FocusSession
import kotlinx.coroutines.flow.Flow

class FocusRepository(
    private val sessionDao: FocusSessionDao,
    private val blockedAppDao: BlockedAppDao,
    private val blockEventDao: BlockEventDao
) {
    val allSessions: Flow<List<FocusSession>> = sessionDao.getAllSessions()
    val blockedApps: Flow<List<BlockedAppConfig>> = blockedAppDao.getAllBlockedApps()
    val recentBlockEvents: Flow<List<BlockEvent>> = blockEventDao.getRecentBlockEvents()

    fun getTodayFocusSeconds(startOfDayMillis: Long): Flow<Int?> =
        sessionDao.getTotalFocusSecondsSince(startOfDayMillis)

    fun getTodaySessionCount(startOfDayMillis: Long): Flow<Int> =
        sessionDao.getSessionCountSince(startOfDayMillis)

    fun getTodayBlockCount(startOfDayMillis: Long): Flow<Int> =
        blockEventDao.getBlockCountSince(startOfDayMillis)

    fun getTodayShortsBlockCount(startOfDayMillis: Long): Flow<Int> =
        blockEventDao.getShortsBlockCountSince(startOfDayMillis)

    suspend fun insertSession(session: FocusSession): Long =
        sessionDao.insertSession(session)

    suspend fun updateAppBlockConfig(config: BlockedAppConfig) =
        blockedAppDao.update(config)

    suspend fun addBlockedApp(config: BlockedAppConfig) =
        blockedAppDao.insertOrUpdate(config)

    suspend fun recordBlockEvent(event: BlockEvent): Long =
        blockEventDao.recordEvent(event)

    suspend fun getActiveBlockedApps(): List<BlockedAppConfig> =
        blockedAppDao.getActiveBlockedApps()

    suspend fun getConfigForPackage(pkg: String): BlockedAppConfig? =
        blockedAppDao.getConfigForPackage(pkg)

    suspend fun clearBlockEvents() = blockEventDao.clearAll()
}
