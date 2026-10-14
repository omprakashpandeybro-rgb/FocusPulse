package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY startTimeMillis DESC")
    fun getAllSessions(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE startTimeMillis >= :sinceMillis ORDER BY startTimeMillis DESC")
    fun getSessionsSince(sinceMillis: Long): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSession): Long

    @Query("SELECT SUM(actualSeconds) FROM focus_sessions WHERE startTimeMillis >= :sinceMillis")
    fun getTotalFocusSecondsSince(sinceMillis: Long): Flow<Int?>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE startTimeMillis >= :sinceMillis")
    fun getSessionCountSince(sinceMillis: Long): Flow<Int>
}
