package com.bignerdranch.android.mapboxplayground

import java.util.*

data class Building (
    var id: String = "",
    var buildingName: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var floors: Int = 0,
    var people: List<Int> = listOf(),
    var densityLevel: Int = 1, // 1, 2 or 3
    var type: String = "non-study", // study, non-study
    var url: String = "https://picsum.photos/300/200"
)