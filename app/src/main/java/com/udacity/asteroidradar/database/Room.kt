package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.getNextSevenDaysFormattedDates
import java.util.Date

@Dao
interface AsteroidDao {
    @Query("select * from AsteroidTable")
    fun getAsteroid(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from AsteroidTable where closeApproachDate between date(:startDate) and date(:endDate) ORDER BY closeApproachDate")
    fun getWeekAsteroid(startDate: String, endDate: String): LiveData<List<Asteroid>>

    @Query("select * from AsteroidTable where closeApproachDate == date(:startDate)")
    fun getTodayAsteroid(startDate : String): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase(){
    abstract val asteroidDao : AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            AsteroidsDatabase::class.java,
            "asteroids").build()
        }
    }
    return INSTANCE
}