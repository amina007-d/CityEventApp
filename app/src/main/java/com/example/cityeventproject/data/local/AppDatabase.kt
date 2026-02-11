package com.example.cityeventproject.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cityeventproject.data.local.dao.EventDao
import com.example.cityeventproject.data.local.entities.EventEntity

@Database(
    entities = [EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}
