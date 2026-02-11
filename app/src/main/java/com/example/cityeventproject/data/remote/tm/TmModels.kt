package com.example.cityeventproject.data.remote.tm

import com.squareup.moshi.Json

data class TmEventsResponse(
    @Json(name = "_embedded") val embedded: Embedded? = null,
    val page: Page? = null
) {
    data class Embedded(val events: List<TmEventDto> = emptyList())
    data class Page(val size: Int = 0, val totalElements: Int = 0, val totalPages: Int = 0, val number: Int = 0)
}

data class TmEventDto(
    val id: String,
    val name: String? = null,
    val url: String? = null,
    val dates: Dates? = null,
    val images: List<ImageDto>? = null,
    val classifications: List<ClassificationDto>? = null,
    val _embedded: EmbeddedDetails? = null
) {
    data class Dates(val start: Start? = null) {
        data class Start(val localDate: String? = null, val localTime: String? = null, val dateTime: String? = null)
    }
    data class ImageDto(val url: String? = null, val width: Int? = null, val height: Int? = null)
    data class ClassificationDto(val segment: Segment? = null) {
        data class Segment(val name: String? = null)
    }
    data class EmbeddedDetails(val venues: List<VenueDto>? = null) {
        data class VenueDto(val name: String? = null, val city: CityDto? = null, val address: AddressDto? = null) {
            data class CityDto(val name: String? = null)
            data class AddressDto(val line1: String? = null)
        }
    }
}
