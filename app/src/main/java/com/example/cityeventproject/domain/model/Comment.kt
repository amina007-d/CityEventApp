package com.example.cityeventproject.domain.model

data class Comment(
    val id: String,
    val eventId: String,
    val uid: String,
    val displayName: String,
    val text: String,
    val createdAt: Long
)
