package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.asDatabaseModel2
import com.udacity.asteroidradar.database.databaseToPictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.database.getDatabase2
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

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
        //getAsteroidItems()
        viewModelScope.launch {
            getPicture()
            asteroidRepository.refreshAsteroids()
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