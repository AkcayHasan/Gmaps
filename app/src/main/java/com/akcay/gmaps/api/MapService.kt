package com.akcay.gmaps.api

import com.akcay.gmaps.api.entities.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapService {

    @GET("directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Result<DirectionsResponse>
}