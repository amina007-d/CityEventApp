package com.example.cityeventproject.data.firebase

import com.example.cityeventproject.domain.model.UserNote
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesDataSource @Inject constructor(
    private val root: DatabaseReference
) {
    private fun notesRef(uid: String) = root.child("users").child(uid).child("notes")

    fun observeNotes(uid: String): Flow<List<UserNote>> = callbackFlow {
        val ref = notesRef(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    val title = child.child("title").getValue(String::class.java) ?: return@mapNotNull null
                    val text = child.child("text").getValue(String::class.java) ?: ""
                    val createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L
                    val updatedAt = child.child("updatedAt").getValue(Long::class.java) ?: createdAt

                    UserNote(
                        id = id,
                        title = title,
                        text = text,
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )
                }.sortedByDescending { it.updatedAt }

                trySend(list).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                // ignore (UI can keep last state)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun getNote(uid: String, noteId: String): UserNote? {
        val snap = notesRef(uid).child(noteId).get().await()
        if (!snap.exists()) return null
        val title = snap.child("title").getValue(String::class.java) ?: return null
        val text = snap.child("text").getValue(String::class.java) ?: ""
        val createdAt = snap.child("createdAt").getValue(Long::class.java) ?: 0L
        val updatedAt = snap.child("updatedAt").getValue(Long::class.java) ?: createdAt
        return UserNote(noteId, title, text, createdAt, updatedAt)
    }

    suspend fun upsert(uid: String, noteId: String?, title: String, text: String) {
        val ref = if (noteId.isNullOrBlank()) notesRef(uid).push() else notesRef(uid).child(noteId)
        val now = System.currentTimeMillis()
        val existing = if (noteId.isNullOrBlank()) null else ref.child("createdAt").get().await().getValue(Long::class.java)
        val createdAt = existing ?: now

        val data = mapOf(
            "title" to title,
            "text" to text,
            "createdAt" to createdAt,
            "updatedAt" to now
        )
        ref.setValue(data).await()
    }

    suspend fun delete(uid: String, noteId: String) {
        notesRef(uid).child(noteId).removeValue().await()
    }
}
