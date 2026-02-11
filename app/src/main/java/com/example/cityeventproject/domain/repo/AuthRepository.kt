package com.example.cityeventproject.domain.repo

import com.example.cityeventproject.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    fun signOut()
}
