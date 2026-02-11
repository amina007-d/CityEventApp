package com.example.cityeventproject.domain.logic

import com.example.cityeventproject.domain.model.Event
import java.time.LocalDate

/**
 * Non-trivial business rule:
 * - Prefer events happening sooner (closer to today)
 * - Prefer events in selected city
 * - Prefer favorites slightly (so they float)
 */
object RecommendationEngine {
    fun score(event: Event, preferredCity: String?, today: LocalDate = LocalDate.now()): Int {
        var score = 0
        val dateScore = runCatching {
            val d = LocalDate.parse(event.date)
            val days = kotlin.math.abs(java.time.temporal.ChronoUnit.DAYS.between(today, d)).toInt()
            // closer date => higher score
            (100 - days).coerceIn(0, 100)
        }.getOrElse { 0 }
        score += dateScore
        if (!preferredCity.isNullOrBlank() && event.city?.equals(preferredCity, ignoreCase = true) == true) score += 25
        if (event.isFavorite) score += 10
        return score
    }

    fun sortRecommended(events: List<Event>, preferredCity: String?, today: LocalDate = LocalDate.now()): List<Event> =
        events.sortedByDescending { score(it, preferredCity, today) }
}
