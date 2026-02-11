package com.example.cityeventproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cityeventproject.data.local.entities.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<EventEntity>)

    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    fun observeAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): EventEntity?

    @Query("SELECT * FROM events WHERE name LIKE '%' || :query || '%' ORDER BY date ASC, time ASC LIMIT :limit")
    suspend fun searchByName(query: String, limit: Int = 50): List<EventEntity>

    @Query("DELETE FROM events")
    suspend fun clearAll()
}
