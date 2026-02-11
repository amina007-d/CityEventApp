package com.example.cityeventproject.data.remote.tm

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmApi {
    @GET("discovery/v2/events/{id}.json")
    suspend fun getEventById(
        @Path("id") id: String,
        @Query("apikey") apiKey: String
    ): TmEventDto

    @GET("discovery/v2/events.json")
    suspend fun getEvents(
        @Query("apikey") apiKey: String,
        @Query("keyword") keyword: String? = null,
        @Query("classificationName") category: String? = null,
        @Query("countryCode") countryCode: String? = null,
        @Query("city") city: String? = null,
        @Query("startDateTime") startDateTime: String? = null,
        @Query("endDateTime") endDateTime: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "date,asc"
    ): TmEventsResponse
}
