package com.example.cityeventproject.data.repo

import com.example.cityeventproject.data.firebase.NotesDataSource
import com.example.cityeventproject.domain.model.UserNote
import com.example.cityeventproject.domain.repo.AuthRepository
import com.example.cityeventproject.domain.repo.NotesRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val authRepo: AuthRepository,
    private val firebaseAuth: FirebaseAuth,
    private val ds: NotesDataSource
) : NotesRepository {

    private fun requireUid(): String =
        firebaseAuth.currentUser?.uid ?: error("Not signed in")

    override fun observeMyNotes(): Flow<List<UserNote>> =
        authRepo.currentUser.flatMapLatest { user ->
            val uid = user?.uid ?: return@flatMapLatest flowOf(emptyList())
            ds.observeNotes(uid)
        }

    override suspend fun getMyNote(noteId: String): UserNote? =
        ds.getNote(requireUid(), noteId)

    override suspend fun upsertMyNote(noteId: String?, title: String, text: String) =
        ds.upsert(requireUid(), noteId, title, text)

    override suspend fun deleteMyNote(noteId: String) =
        ds.delete(requireUid(), noteId)
}
