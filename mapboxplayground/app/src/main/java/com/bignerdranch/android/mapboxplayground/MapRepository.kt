package com.bignerdranch.android.mapboxplayground

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MapRepository private constructor(context: Context) {

    var buildings: MutableLiveData<List<Building>> = MutableLiveData()
    var pathFastest: MutableLiveData<List<List<Double>>> = MutableLiveData()
    var pathSafest: MutableLiveData<List<List<Double>>> = MutableLiveData()

    private val retrofit: Retrofit = Retrofit.Builder().baseUrl("https://density-dodger.herokuapp.com/api/")
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val backendApi: BackendRequests = retrofit.create(BackendRequests::class.java)

    fun loadRoute(userLat: Double, userLon: Double, locationId: String, safe: Boolean) {
        val routeRequest: Call<List<List<Double>>> = backendApi.loadRoute(userLat, userLon, locationId, safe)
        //val routeRequest: Call<List<List<Double>>> = backendApi.testRoute()

        routeRequest.enqueue(object : Callback<List<List<Double>>> {
            override fun onFailure(call: Call<List<List<Double>>>, t: Throwable) {
                Log.e("APIFAIL", "Failed to fetch routes", t)
            }

            override fun onResponse(call: Call<List<List<Double>>>, response: Response<List<List<Double>>>) {
                var pathCoordinates: List<List<Double>>? = response.body()

                if (pathCoordinates != null) {
                    when (safe) {
                        true -> pathSafest.value = pathCoordinates
                        false -> pathFastest.value = pathCoordinates
                    }
                }
            }
        })
    }

    fun getBuildingFromId(buildingId: String): Building? {
        // find the building with the given id in our repository
        for (i in 0 until (buildings.value?.size ?: 0)) {
            var buildingFromRepo = buildings.value?.get(i)

            if (buildingFromRepo != null) {
                if (buildingFromRepo.id == buildingId) {
                    // store the obtained building in a variable
                    return buildingFromRepo
                }
            }
        }

        return null
    }

    private fun loadBuildings() {
        val buildingsRequest: Call<List<Building>> = backendApi.fetchBuildings()

        buildingsRequest.enqueue(object : Callback<List<Building>> {
            override fun onFailure(call: Call<List<Building>>, t: Throwable) {
                Log.e("APIFAIL", "Failed to fetch buildings", t)
                // retry
                loadBuildings()
            }

            override fun onResponse(call: Call<List<Building>>, response: Response<List<Building>>) {
                var buildingItems: List<Building>? = response.body()

                if (buildingItems != null) {
                    buildings.value = buildingItems as MutableList<Building>
                }
            }
        })
    }

    fun clearPaths() {
        pathFastest.value = listOf()
        pathSafest.value = listOf()
    }


    // used for adding nodes into map
    init{
        loadBuildings()
    }

    companion object {
        private var INSTANCE: MapRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MapRepository(context)
            }
        }

        fun get(): MapRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}