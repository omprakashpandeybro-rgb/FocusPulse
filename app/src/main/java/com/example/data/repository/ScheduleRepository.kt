package com.example.data.repository

import com.example.data.db.ScheduleDao
import com.example.data.model.ScheduleItem
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val scheduleDao: ScheduleDao) {
    val allItems: Flow<List<ScheduleItem>> = scheduleDao.getAllScheduleItems()

    suspend fun getAlarmEnabledItems(): List<ScheduleItem> = scheduleDao.getAlarmEnabledItems()

    suspend fun getItemById(id: Long): ScheduleItem? = scheduleDao.getItemById(id)

    suspend fun insertItem(item: ScheduleItem): Long = scheduleDao.insertItem(item)

    suspend fun updateItem(item: ScheduleItem) = scheduleDao.updateItem(item)

    suspend fun deleteItem(item: ScheduleItem) = scheduleDao.deleteItem(item)

    suspend fun toggleCompleted(id: Long, completed: Boolean) = scheduleDao.setCompleted(id, completed)

    suspend fun replaceWithTemplate(items: List<ScheduleItem>) {
        scheduleDao.deleteAll()
        scheduleDao.insertAll(items)
    }
}
