package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

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
//_________________________________________________________________
@Dao
interface PictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPic(data: PictureOfDayDB)

    @Query("select * from pictureTable")
    fun getDbPicture(): PictureOfDayDB
}

@Database(entities = [PictureOfDayDB::class], version = 1)
abstract class PictureDatabase : RoomDatabase() {
    abstract fun pictureDao(): PictureDao
}
private lateinit var INSTANCE2: PictureDatabase

fun getDatabase2(context: Context): PictureDatabase {
    synchronized(PictureDatabase::class.java) {
        if (!::INSTANCE2.isInitialized) {
            INSTANCE2 = Room.databaseBuilder(context.applicationContext,
            PictureDatabase::class.java,
            "pictures").build()
        }
    }
    return INSTANCE2
}
