package com.example.cityeventproject.domain.repo

import com.example.cityeventproject.domain.model.UserNote
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun observeMyNotes(): Flow<List<UserNote>>
    suspend fun getMyNote(noteId: String): UserNote?
    suspend fun upsertMyNote(noteId: String?, title: String, text: String)
    suspend fun deleteMyNote(noteId: String)
}
