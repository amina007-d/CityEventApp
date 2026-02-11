package com.example.cityeventproject.domain.logic

object Validators {

    fun isValidCountryCode(code: String): Boolean {
        val c = code.trim()
        return c.length == 2 && c.all { it.isLetter() }
    }

    fun isValidEmail(email: String): Boolean =
        email.trim().matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s]{2,}$"))

    fun validatePassword(password: String): String? {
        val p = password.trim()
        if (p.length < 6) return "Password must be at least 6 characters."
        if (!p.any { it.isDigit() }) return "Password must contain at least one number."
        return null
    }

    fun validateComment(text: String): String? {
        val t = text.trim()
        if (t.isBlank()) return "Comment cannot be empty."
        if (t.length < 3) return "Comment is too short."
        if (t.length > 280) return "Comment is too long (max 280)."
        return null
    }

    fun validateNoteTitle(title: String): String? {
        val t = title.trim()
        if (t.isBlank()) return "Title is required."
        if (t.length < 3) return "Title is too short (min 3)."
        if (t.length > 60) return "Title is too long (max 60)."
        return null
    }

    fun validateNoteText(text: String): String? {
        val t = text.trim()
        if (t.isBlank()) return "Note text cannot be empty."
        if (t.length < 10) return "Note text is too short (min 10)."
        if (t.length > 1000) return "Note text is too long (max 1000)."
        return null
    }
}
