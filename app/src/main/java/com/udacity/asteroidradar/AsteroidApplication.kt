package com.udacity.asteroidradar

import android.app.Application
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso

class AsteroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttpDownloader(this, Long.MAX_VALUE))
        val build = builder.build()
        build.setIndicatorsEnabled(true)
        build.isLoggingEnabled = true
        Picasso.setSingletonInstance(build)
    }
}