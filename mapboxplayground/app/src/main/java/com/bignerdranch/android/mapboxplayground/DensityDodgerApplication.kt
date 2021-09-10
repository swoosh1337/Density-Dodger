package com.bignerdranch.android.mapboxplayground

import android.app.Application

class DensityDodgerApplication: Application() {

    override fun onCreate(){
        super.onCreate()
        MapRepository.initialize(this)

    }

}