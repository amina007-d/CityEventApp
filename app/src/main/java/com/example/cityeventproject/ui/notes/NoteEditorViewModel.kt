package com.example.cityeventproject.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityeventproject.domain.logic.Validators
import com.example.cityeventproject.domain.model.UserNote
import com.example.cityeventproject.domain.repo.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val repo: NotesRepository
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loadedNote = MutableStateFlow<UserNote?>(null)
    val loadedNote: StateFlow<UserNote?> = _loadedNote.asStateFlow()

    fun setTitle(v: String) { _title.value = v }
    fun setText(v: String) { _text.value = v }

    fun load(noteId: String?) {
        if (noteId.isNullOrBlank()) return
        if (_loadedNote.value?.id == noteId) return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val note = repo.getMyNote(noteId)
                _loadedNote.value = note
                if (note != null) {
                    _title.value = note.title
                    _text.value = note.text
                }
            } catch (t: Throwable) {
                _error.value = t.message ?: "Failed to load note"
            } finally {
                _loading.value = false
            }
        }
    }

    fun save(noteId: String?, onDone: () -> Unit) {
        val titleErr = Validators.validateNoteTitle(_title.value)
        if (titleErr != null) { _error.value = titleErr; return }
        val textErr = Validators.validateNoteText(_text.value)
        if (textErr != null) { _error.value = textErr; return }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repo.upsertMyNote(noteId, _title.value.trim(), _text.value.trim())
                onDone()
            } catch (t: Throwable) {
                _error.value = t.message ?: "Failed to save"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
}
