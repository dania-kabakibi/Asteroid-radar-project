package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class MainViewModel : ViewModel() {
    private val _asteroidItems = MutableLiveData<List<Asteroid>>()
    val asteroidItems: LiveData<List<Asteroid>>
        get() = _asteroidItems

    private val _imageOfTheDay = MutableLiveData<PictureOfDay>()
    val imageOfTheDay: LiveData<PictureOfDay>
        get() = _imageOfTheDay

    private val _navigateToSelectedProperty = MutableLiveData<Asteroid>()
    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedProperty

    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedProperty.value = asteroid
    }

    private fun getAsteroidItems() {
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
    }

    private suspend fun getPicture() {
        withContext(Dispatchers.IO) {
            try {
                _imageOfTheDay.postValue(AsteroidApi.retrofitService.getPictureOfDay())
                Log.i("mainViewModel", "Success:${_imageOfTheDay.value}")
            } catch (e: Exception) {
                Log.i("mainViewModel", "Failure:${e.message}")
            }
        }
    }

    init {
        getAsteroidItems()
        viewModelScope.launch { getPicture() }
    }

}