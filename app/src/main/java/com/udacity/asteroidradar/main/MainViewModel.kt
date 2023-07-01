package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.*
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

enum class Options { SHOW_ALL, SHOW_TODAY, SHOW_WEEK }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val database2 = getDatabase2(application)

    var optionMenu = MutableLiveData(Options.SHOW_ALL)

    var asteroids: LiveData<List<Asteroid>?> =
        Transformations.switchMap(optionMenu) {
            when (it) {
                Options.SHOW_TODAY -> asteroidRepository.asteroidOfToday
                Options.SHOW_WEEK -> asteroidRepository.asteroidOfWeek
                else -> asteroidRepository.allAsteroids
            }
        }

    init {
        viewModelScope.launch {
            getPicture()
            scheduleWork()
            //asteroidRepository.refreshAsteroids()
        }
    }

    private val _imageOfTheDay = MutableLiveData<PictureOfDay>()
    val imageOfTheDay: LiveData<PictureOfDay>
        get() = _imageOfTheDay

    private val _navigateToSelectedProperty = MutableLiveData<Asteroid>()
    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedProperty

    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedProperty.value = asteroid
    }

    private suspend fun getPicture() {
        withContext(Dispatchers.IO) {
            try {
                _imageOfTheDay.postValue(AsteroidApi.retrofitService.getPictureOfDay())
                database2.pictureDao().insertPic(asDatabaseModel2(_imageOfTheDay.value!!))
            } catch (e: Exception) {
                _imageOfTheDay.postValue(databaseToPictureOfDay(database2.pictureDao().getDbPicture()))
            }
        }
    }

    private fun scheduleWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val workRequest =
            PeriodicWorkRequestBuilder<WorkerClass>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance().enqueue(workRequest)
    }
}


//val asteroids = asteroidRepository.asteroids

/*private val _asteroidItems = MutableLiveData<List<Asteroid>>()
val asteroidItems: LiveData<List<Asteroid>>
    get() = _asteroidItems*/

/*private fun getAsteroidItems() {
        AsteroidApi.retrofitService.getAsteroids().enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                _asteroidItems.value =
                    response.body()?.let { parseAsteroidsJsonResult(JSONObject(it)) }
                //Log.i("mainViewModel", "Success:${_asteroidItems.toString()}")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                //Log.i("mainViewModel", "Failure:${t.message}")
            }
        })
    }*/