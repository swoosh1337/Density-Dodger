package com.bignerdranch.android.mapboxplayground

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BackendRequests {
    @GET("buildings/all")
    fun fetchBuildings(): Call<List<Building>>

    @GET("https://density-dodger.herokuapp.com/api/navigation/route?")
    fun loadRoute(
        @Query("from_latitude") userLat: Double,
        @Query("from_longitude") userLon: Double,
        @Query("to") locationId: String,
        @Query("safe") safe: Boolean
    ): Call<List<List<Double>>>

    @GET("https://density-dodger.herokuapp.com/api/navigation/route?from_latitude=42.4213&from_longitude=-72.3843&to=FIS")
    fun testRoute(): Call<List<List<Double>>>
}