package com.example.cityeventproject.domain.repo

import androidx.paging.PagingData
import com.example.cityeventproject.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun pagedEvents(
        countryCode: String?,     // NEW: "ALL" или null = везде
        city: String?,
        category: String?,
        keyword: String?,
        startDateTime: String? = null,
        endDateTime: String? = null
    ): Flow<PagingData<Event>>

    suspend fun getEventById(eventId: String, forceNetwork: Boolean = false): Event? // NEW param
    suspend fun searchCache(query: String): List<Event>
    suspend fun setFavorite(eventId: String, isFav: Boolean)
    fun observeFavorites(): Flow<Set<String>>
}

