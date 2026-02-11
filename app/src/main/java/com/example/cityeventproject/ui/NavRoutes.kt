package com.example.cityeventproject.ui

object NavRoutes {
    const val AUTH = "auth"
    const val FEED = "feed"
    const val SEARCH = "search"
    const val PROFILE = "profile"
    const val DETAILS = "details/{eventId}"
    const val COMMENTS = "comments/{eventId}"

    // Create/Edit (user notes)
    const val NOTE_EDITOR = "noteEditor?noteId={noteId}"

    fun details(eventId: String) = "details/$eventId"
    fun comments(eventId: String) = "comments/$eventId"
    fun noteEditor(noteId: String? = null) = if (noteId.isNullOrBlank()) "noteEditor" else "noteEditor?noteId=$noteId"
}
