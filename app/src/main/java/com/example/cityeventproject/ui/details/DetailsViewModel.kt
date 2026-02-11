package com.example.cityeventproject.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityeventproject.domain.model.Event
import com.example.cityeventproject.domain.repo.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repo: EventRepository
) : ViewModel() {

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(eventId: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                _event.value = repo.getEventById(eventId)
                if (_event.value == null) _error.value = "Event not found in cache yet. Go back and refresh."
            } catch (t: Throwable) {
                _error.value = t.message ?: "Failed to load"
            }
        }
    }

    fun toggleFavorite(eventId: String, isFav: Boolean) {
        viewModelScope.launch {
            _error.value = null
            try {
                repo.setFavorite(eventId, isFav)
                // Optimistic UI update so button text changes immediately
                val current = _event.value
                if (current != null && current.id == eventId) {
                    _event.value = current.copy(isFavorite = isFav)
                }
            } catch (t: Throwable) {
                _error.value = t.message ?: "Failed to update favorite"
            }
        }
    }
}
