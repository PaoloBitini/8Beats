package com.example.a8beats

import android.app.Application
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.location.DefaultLocationProvider

class SearchApplication() : Application() {

    override fun onCreate() {
        super.onCreate()
        MapboxSearchSdk.initialize(
            this,
            getString(R.string.access_token_mapbox),
            DefaultLocationProvider(this)
        )
    }
}


