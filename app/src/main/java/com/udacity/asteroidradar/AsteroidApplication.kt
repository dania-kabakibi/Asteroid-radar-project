package com.udacity.asteroidradar

import android.app.Application
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso


class AsteroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        val build = builder.build()
        build.setIndicatorsEnabled(true)
        build.isLoggingEnabled = true
        Picasso.setSingletonInstance(build)
    }
}