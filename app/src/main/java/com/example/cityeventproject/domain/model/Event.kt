package com.example.cityeventproject.domain.model

data class Event(
    val id: String,
    val name: String,
    val date: String,
    val time: String?,
    val city: String?,
    val venue: String?,
    val address: String?,
    val category: String?,
    val imageUrl: String?,
    val url: String?,
    val isFavorite: Boolean
)
