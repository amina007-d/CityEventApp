package com.example.cityeventproject.domain.model

data class UserNote(
    val id: String,
    val title: String,
    val text: String,
    val createdAt: Long,
    val updatedAt: Long
)
