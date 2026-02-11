package com.example.cityeventproject.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.cityeventproject.data.firebase.AuthDataSource
import com.example.cityeventproject.data.firebase.FavoritesDataSource
import com.example.cityeventproject.data.local.dao.EventDao
import com.example.cityeventproject.data.toEntity
import com.example.cityeventproject.data.toDomain
import com.example.cityeventproject.data.remote.tm.TmApi
import com.example.cityeventproject.domain.model.Event
import com.example.cityeventproject.domain.repo.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val api: TmApi,
    private val dao: EventDao,
    private val authDs: AuthDataSource,
    private val favoritesDs: FavoritesDataSource
) : EventRepository {

    private val favoritesFlow = MutableStateFlow<Set<String>>(emptySet())

    override fun observeFavorites(): Flow<Set<String>> {
        val u = authDs.currentUser
        return if (u == null) MutableStateFlow(emptySet())
        else favoritesDs.observeFavoriteIds(u.uid).map { ids ->
            favoritesFlow.value = ids
            ids
        }
    }

    override fun pagedEvents(
        countryCode: String?,
        city: String?,
        category: String?,
        keyword: String?,
        startDateTime: String?,
        endDateTime: String?
    ): Flow<PagingData<Event>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                EventsPagingSource(
                    api = api,
                    dao = dao,
                    favorites = { favoritesFlow.value },
                    city = city,
                    countryCode = countryCode,
                    category = category,
                    keyword = keyword,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime
                )
            }
        ).flow
    }

    override suspend fun getEventById(eventId: String, forceNetwork: Boolean): Event? {
        val favs = favoritesFlow.value

        // Fast path: cached
        val cached = dao.getById(eventId)
        if (cached != null && !forceNetwork) {
            return cached.toDomain(isFavorite = favs.contains(eventId))
        }

        // Optional refresh from network (details endpoint)
        if (forceNetwork && com.example.cityeventproject.BuildConfig.TM_API_KEY.isNotBlank()) {
            return try {
                val nowMs = System.currentTimeMillis()
                val dto = api.getEventById(eventId, com.example.cityeventproject.BuildConfig.TM_API_KEY)
                val merged = com.example.cityeventproject.domain.logic.SyncPolicy.merge(cached, dto.toEntity(nowMs))
                dao.upsertAll(listOf(merged))
                merged.toDomain(isFavorite = favs.contains(eventId))
            } catch (_: Throwable) {
                cached?.toDomain(isFavorite = favs.contains(eventId))
            }
        }

        return cached?.toDomain(isFavorite = favs.contains(eventId))
    }

    override suspend fun searchCache(query: String): List<Event> {
        val favs = favoritesFlow.value
        return dao.searchByName(query).map { it.toDomain(isFavorite = favs.contains(it.id)) }
    }

    override suspend fun setFavorite(eventId: String, isFav: Boolean) {
        val u = authDs.currentUser ?: error("Not signed in")
        favoritesDs.setFavorite(u.uid, eventId, isFav)
    }
}
