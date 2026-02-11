package com.example.cityeventproject.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityeventproject.domain.model.Event
import com.example.cityeventproject.domain.model.UserNote
import com.example.cityeventproject.domain.model.UserProfile
import com.example.cityeventproject.domain.repo.AuthRepository
import com.example.cityeventproject.domain.repo.EventRepository
import com.example.cityeventproject.domain.repo.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOf
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val eventsRepo: EventRepository,
    private val notesRepo: NotesRepository
) : ViewModel() {

    val user: StateFlow<UserProfile?> =
        authRepo.currentUser.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val favorites: StateFlow<Set<String>> =
        eventsRepo.observeFavorites().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val favoriteEvents: StateFlow<List<Event>> =
        combine(user, favorites) { u, favIds ->
            if (u == null) emptySet<String>() else favIds
        }
            .flatMapLatest { favIds ->
                if (favIds.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    flow<List<Event>> {
                        val events = supervisorScope {
                            favIds.toList().map { id ->
                                async { eventsRepo.getEventById(id) }
                            }.awaitAll().filterNotNull()
                        }.sortedBy { it.date }
                        emit(events)
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val notes: StateFlow<List<UserNote>> =
        notesRepo.observeMyNotes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteNote(noteId: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try { notesRepo.deleteMyNote(noteId) }
            catch (t: Throwable) { onError(t.message ?: "Failed to delete") }
        }
    }

    fun removeFavorite(eventId: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try { eventsRepo.setFavorite(eventId, false) }
            catch (t: Throwable) { onError(t.message ?: "Failed to remove") }
        }
    }

    fun signOut() = authRepo.signOut()
}
