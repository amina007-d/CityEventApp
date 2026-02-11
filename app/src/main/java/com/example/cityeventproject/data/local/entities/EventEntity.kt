package com.example.cityeventproject.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val name: String,
    val date: String,
    val time: String?,
    val city: String?,
    val venue: String?,
    val address: String?,
    val category: String?,
    val imageUrl: String?,
    val url: String?,
    val lastUpdatedEpochMs: Long
)
