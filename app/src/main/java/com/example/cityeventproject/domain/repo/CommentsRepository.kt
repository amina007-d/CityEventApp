package com.example.cityeventproject.domain.repo

import com.example.cityeventproject.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentsRepository {
    fun observeComments(eventId: String): Flow<List<Comment>>
    suspend fun add(eventId: String, text: String)
    suspend fun update(eventId: String, commentId: String, text: String)
    suspend fun delete(eventId: String, commentId: String)
}
