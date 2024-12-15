package com.jdcoding.conteurifydimytaximeter

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {
    @GET("maps/api/directions/json")
    fun getRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Call<DirectionsResponse>
}