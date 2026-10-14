package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BlockEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockEventDao {
    @Query("SELECT * FROM block_events ORDER BY timestamp DESC LIMIT 100")
    fun getRecentBlockEvents(): Flow<List<BlockEvent>>

    @Query("SELECT COUNT(*) FROM block_events WHERE timestamp >= :sinceMillis")
    fun getBlockCountSince(sinceMillis: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM block_events WHERE wasShorts = 1 AND timestamp >= :sinceMillis")
    fun getShortsBlockCountSince(sinceMillis: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordEvent(event: BlockEvent): Long

    @Query("DELETE FROM block_events")
    suspend fun clearAll()
}
