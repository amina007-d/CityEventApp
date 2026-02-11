package com.example.cityeventproject.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityeventproject.domain.model.Event
import com.example.cityeventproject.domain.repo.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: EventRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<List<Event>>(emptyList())
    val results: StateFlow<List<Event>> = _results

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            _query
                .debounce(450)
                .map { it.trim() }
                .distinctUntilChanged()
                .collect { q ->
                    _error.value = null
                    if (q.isBlank()) {
                        _results.value = emptyList()
                    } else {
                        try {
                            _results.value = repo.searchCache(q)
                        } catch (t: Throwable) {
                            _error.value = t.message ?: "Search failed"
                        }
                    }
                }
        }
    }

    fun setQuery(v: String) {
        _query.value = v
    }
}
