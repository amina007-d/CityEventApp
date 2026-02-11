package com.example.cityeventproject.data.repo

import com.example.cityeventproject.data.firebase.AuthDataSource
import com.example.cityeventproject.domain.model.UserProfile
import com.example.cityeventproject.domain.repo.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDs: AuthDataSource,
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val u = auth.currentUser
            if (u == null) trySend(null)
            else {
                val email = u.email
                val display = u.displayName ?: email?.substringBefore("@") ?: "User"
                trySend(UserProfile(uid = u.uid, email = email, displayName = display))
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String) = authDs.signIn(email, password)
    override suspend fun signUp(email: String, password: String) = authDs.signUp(email, password)
    override fun signOut() = authDs.signOut()
}
