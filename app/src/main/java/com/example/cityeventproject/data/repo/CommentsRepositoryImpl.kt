package com.example.cityeventproject.data.repo

import com.example.cityeventproject.data.firebase.AuthDataSource
import com.example.cityeventproject.data.firebase.CommentsDataSource
import com.example.cityeventproject.domain.model.Comment
import com.example.cityeventproject.domain.repo.CommentsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentsRepositoryImpl @Inject constructor(
    private val authDs: AuthDataSource,
    private val commentsDs: CommentsDataSource
) : CommentsRepository {
    override fun observeComments(eventId: String) = commentsDs.observeComments(eventId)

    override suspend fun add(eventId: String, text: String) {
        val u = authDs.currentUser ?: error("Not signed in")
        val display = u.displayName ?: u.email?.substringBefore("@") ?: "User"
        commentsDs.addComment(eventId, u.uid, display, text)
    }

    override suspend fun update(eventId: String, commentId: String, text: String) {
        commentsDs.updateComment(eventId, commentId, text)
    }

    override suspend fun delete(eventId: String, commentId: String) {
        commentsDs.deleteComment(eventId, commentId)
    }
}
