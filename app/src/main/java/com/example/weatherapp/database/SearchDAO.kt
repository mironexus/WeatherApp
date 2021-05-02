package com.example.weatherapp.database

import androidx.room.*
import com.example.weatherapp.model.SearchLocation

@Dao
interface SearchDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSearchLocation(searchLocation: SearchLocation)

    @Query("SELECT * FROM search_locations ORDER BY timestamp DESC")
    suspend fun getAllSearchLocations() : MutableList<SearchLocation>

    @Query("SELECT woeid FROM search_locations ORDER BY timestamp DESC")
    suspend fun getAllSearchWoeids() : List<Int>

    @Query("SELECT * FROM search_locations WHERE woeid = :woeid")
    suspend fun getSingleLocation(woeid: Int) : SearchLocation

    @Query("DELETE FROM search_locations")
    suspend fun deleteAllSearchLocations()

}