package com.example.cityeventproject.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cityeventproject.BuildConfig
import com.example.cityeventproject.data.local.dao.EventDao
import com.example.cityeventproject.data.remote.safeApiCall
import com.example.cityeventproject.data.remote.tm.TmApi
import com.example.cityeventproject.data.toEntity
import com.example.cityeventproject.domain.logic.SyncPolicy
import com.example.cityeventproject.domain.model.Event
import com.example.cityeventproject.data.toDomain
import kotlinx.coroutines.flow.first

class EventsPagingSource(
    private val api: TmApi,
    private val dao: EventDao,
    private val favorites: suspend () -> Set<String>,
    private val city: String?,
    private val countryCode: String?,
    private val category: String?,
    private val keyword: String?,
    private val startDateTime: String?,
    private val endDateTime: String?
) : PagingSource<Int, Event>() {

    private fun nowIsoUtc(): String {
        // Ticketmaster expects ISO-8601 UTC like: 2026-02-11T12:34:56Z
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        return sdf.format(java.util.Date())
    }


    override fun getRefreshKey(state: PagingState<Int, Event>): Int? =
        state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Event> {
        val page = params.key ?: 0
        val nowMs = System.currentTimeMillis()

        if (BuildConfig.TM_API_KEY.isBlank()) {
            return LoadResult.Error(IllegalStateException("Missing TM_API_KEY in local.properties"))
        }

        return when (val res = safeApiCall {
            api.getEvents(
                apiKey = BuildConfig.TM_API_KEY,
                keyword = keyword?.takeIf { it.isNotBlank() },
                city = city?.takeIf { it.isNotBlank() },
                countryCode = countryCode?.takeIf { it.isNotBlank() },
                category = category?.takeIf { it.isNotBlank() },
                // IMPORTANT: default to "now" so we don't accidentally show past/old events.
                startDateTime = (startDateTime ?: nowIsoUtc()),
                endDateTime = endDateTime,
                page = page,
                size = params.loadSize
            )
        }) {
            is com.example.cityeventproject.data.remote.NetworkResult.Success -> {
                val dto = res.value
                val remoteEvents = dto.embedded?.events ?: emptyList()
                // cache
                val entities = remoteEvents.map { it.toEntity(nowMs) }
                // Merge policy with existing cache
                val merged = entities.map { e ->
                    val cached = dao.getById(e.id)
                    SyncPolicy.merge(cached, e)
                }
                dao.upsertAll(merged)

                val favs = favorites()
                val items = merged.map { it.toDomain(isFavorite = favs.contains(it.id)) }

                val totalPages = dto.page?.totalPages ?: (if (items.isEmpty()) 0 else page + 1)
                val nextKey = if (page + 1 < totalPages) page + 1 else null
                val prevKey = if (page == 0) null else page - 1
                LoadResult.Page(data = items, prevKey = prevKey, nextKey = nextKey)
            }
            is com.example.cityeventproject.data.remote.NetworkResult.HttpError ->
                LoadResult.Error(RuntimeException("HTTP ${res.code}: ${res.message}"))
            is com.example.cityeventproject.data.remote.NetworkResult.NetworkError -> {
                // Offline fallback: serve cache (first page only)
                if (page > 0) {
                    LoadResult.Page(emptyList(), prevKey = page - 1, nextKey = null)
                } else {
                    val favs = favorites()
                    val cached = dao.observeAll().first()
                    val items = cached.map { it.toDomain(isFavorite = favs.contains(it.id)) }
                    LoadResult.Page(items, prevKey = null, nextKey = null)
                }
            }
            is com.example.cityeventproject.data.remote.NetworkResult.UnknownError ->
                LoadResult.Error(RuntimeException(res.message))
        }
    }
}
