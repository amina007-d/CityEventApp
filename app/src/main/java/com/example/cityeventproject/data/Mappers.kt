package com.example.cityeventproject.data

import com.example.cityeventproject.data.local.entities.EventEntity
import com.example.cityeventproject.data.remote.tm.TmEventDto
import com.example.cityeventproject.domain.model.Event

fun TmEventDto.toEntity(nowMs: Long): EventEntity {
    val img = images
        ?.filter { (it.width ?: 0) >= 600 }
        ?.maxByOrNull { (it.width ?: 0) }
        ?.url
        ?: images?.firstOrNull()?.url

    val category = classifications?.firstOrNull()?.segment?.name
    val venue = _embedded?.venues?.firstOrNull()
    val date = dates?.start?.localDate ?: ""
    val time = dates?.start?.localTime

    return EventEntity(
        id = id,
        name = name ?: "Unnamed event",
        date = date,
        time = time,
        city = venue?.city?.name,
        venue = venue?.name,
        address = venue?.address?.line1,
        category = category,
        imageUrl = img,
        url = url,
        lastUpdatedEpochMs = nowMs
    )
}

fun EventEntity.toDomain(isFavorite: Boolean): Event = Event(
    id = id,
    name = name,
    date = date,
    time = time,
    city = city,
    venue = venue,
    address = address,
    category = category,
    imageUrl = imageUrl,
    url = url,
    isFavorite = isFavorite
)
