package com.example.cityeventproject.ui.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityeventproject.domain.logic.Validators
import com.example.cityeventproject.domain.model.Comment
import com.example.cityeventproject.domain.repo.CommentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val repo: CommentsRepository
) : ViewModel() {

    fun observe(eventId: String): StateFlow<List<Comment>> =
        repo.observeComments(eventId).stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(eventId: String, text: String, onError: (String) -> Unit) {
        val err = Validators.validateComment(text)
        if (err != null) return onError(err)
        viewModelScope.launch {
            try { repo.add(eventId, text.trim()) }
            catch (t: Throwable) { onError(t.message ?: "Failed to add") }
        }
    }

    fun update(eventId: String, commentId: String, text: String, onError: (String) -> Unit) {
        val err = Validators.validateComment(text)
        if (err != null) return onError(err)
        viewModelScope.launch {
            try { repo.update(eventId, commentId, text.trim()) }
            catch (t: Throwable) { onError(t.message ?: "Failed to update") }
        }
    }

    fun delete(eventId: String, commentId: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            try { repo.delete(eventId, commentId) }
            catch (t: Throwable) { onError(t.message ?: "Failed to delete") }
        }
    }
}
