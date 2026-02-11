package com.example.cityeventproject.data.firebase

import com.example.cityeventproject.domain.model.Comment
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
class CommentsDataSource @Inject constructor(
    private val root: DatabaseReference
) {
    private fun commentsRef(eventId: String) = root.child("comments").child(eventId)

    fun observeComments(eventId: String): Flow<List<Comment>> = callbackFlow {
        val ref = commentsRef(eventId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    val id = child.key ?: return@mapNotNull null
                    val uid = child.child("uid").getValue(String::class.java) ?: return@mapNotNull null
                    val displayName = child.child("displayName").getValue(String::class.java) ?: "User"
                    val text = child.child("text").getValue(String::class.java) ?: ""
                    val createdAt = child.child("createdAt").getValue(Long::class.java) ?: 0L

                    Comment(
                        id = id,
                        eventId = eventId,
                        uid = uid,
                        displayName = displayName,
                        text = text,
                        createdAt = createdAt
                    )
                }.sortedByDescending { it.createdAt }

                trySend(list).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    suspend fun addComment(eventId: String, uid: String, displayName: String, text: String) {
        val ref = commentsRef(eventId).push()
        val data = mapOf(
            "uid" to uid,
            "displayName" to displayName,
            "text" to text,
            "createdAt" to System.currentTimeMillis()
        )
        ref.setValue(data).await()
    }

    suspend fun updateComment(eventId: String, commentId: String, text: String) {
        commentsRef(eventId).child(commentId).child("text").setValue(text).await()
    }

    suspend fun deleteComment(eventId: String, commentId: String) {
        commentsRef(eventId).child(commentId).removeValue().await()
    }
}
