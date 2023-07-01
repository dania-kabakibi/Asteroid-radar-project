package com.udacity.asteroidradar

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository

class WorkerClass(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val asteroidRepository = AsteroidRepository(database)
        return try {
            asteroidRepository.refreshAsteroids()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}