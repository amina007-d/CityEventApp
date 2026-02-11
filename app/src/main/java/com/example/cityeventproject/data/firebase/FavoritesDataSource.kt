package com.example.cityeventproject.data.firebase

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
class FavoritesDataSource @Inject constructor(
    private val root: DatabaseReference
) {
    private fun favRef(uid: String) = root.child("users").child(uid).child("favorites")

    fun observeFavoriteIds(uid: String): Flow<Set<String>> = callbackFlow {
        val ref = favRef(uid)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // favorites/{eventId}: true
                val ids = snapshot.children.mapNotNull { it.key }.toSet()
                trySend(ids).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                // Можно отправить пусто или игнорировать
                // trySend(emptySet())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }

    suspend fun setFavorite(uid: String, eventId: String, isFav: Boolean) {
        val ref = favRef(uid).child(eventId)
        if (isFav) {
            ref.setValue(true).await()
        } else {
            ref.removeValue().await()
        }
    }
}
