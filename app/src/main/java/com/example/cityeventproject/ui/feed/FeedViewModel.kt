package com.example.cityeventproject.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cityeventproject.domain.model.Event
import com.example.cityeventproject.domain.repo.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repo: EventRepository
) : ViewModel() {

    private val countryCode = MutableStateFlow<String?>(null)
    private val city = MutableStateFlow<String?>(null)
    private val category = MutableStateFlow<String?>(null)
    private val keyword = MutableStateFlow<String?>(null)
    private val startDateTime = MutableStateFlow<String?>(null)
    private val endDateTime = MutableStateFlow<String?>(null)

    val favorites: StateFlow<Set<String>> =
        repo.observeFavorites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    private data class Filters(
        val countryCode: String?,
        val city: String?,
        val category: String?,
        val keyword: String?,
        val startDateTime: String?,
        val endDateTime: String?
    )
    // Debounced keyword to avoid network call on every keystroke (requirement 4.4)
    private val debouncedKeyword = keyword.debounce(450)

    val paging: Flow<PagingData<Event>> =
        combine(
            countryCode,
            city,
            category,
            debouncedKeyword,
            startDateTime,
            endDateTime
        ) { arr: Array<Any?> ->
            Filters(
                countryCode = arr[0] as String?,
                city = arr[1] as String?,
                category = arr[2] as String?,
                keyword = arr[3] as String?,
                startDateTime = arr[4] as String?,
                endDateTime = arr[5] as String?
            )
        }
            .flatMapLatest { f ->
                repo.pagedEvents(
                    countryCode = f.countryCode,
                    city = f.city,
                    category = f.category,
                    keyword = f.keyword,
                    startDateTime = f.startDateTime,
                    endDateTime = f.endDateTime
                )
            }
            .cachedIn(viewModelScope)

    fun setFilters(
        countryCode: String?,
        city: String?,
        category: String?,
        keyword: String?,
        startDateTime: String? = null,
        endDateTime: String? = null
    ) {
        this.countryCode.value = countryCode?.ifBlank { null }
        this.city.value = city?.ifBlank { null }
        this.category.value = category?.ifBlank { null }
        this.keyword.value = keyword?.ifBlank { null }
        this.startDateTime.value = startDateTime?.ifBlank { null }
        this.endDateTime.value = endDateTime?.ifBlank { null }
    }

    fun toggleFavorite(eventId: String, isFav: Boolean) {
        viewModelScope.launch { repo.setFavorite(eventId, isFav) }
    }
}
