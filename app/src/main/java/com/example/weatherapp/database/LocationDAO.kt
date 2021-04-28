//package com.example.weatherapp.database
//
//import androidx.room.*
//import com.example.weatherapp.model.Location
//
//@Dao
//interface LocationDAO {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertLocation(location: Location)
//
//    @Update
//    suspend fun updateLocation(location: Location)
//
//    @Delete
//    suspend fun deleteLocation(location: Location)
//
//    @Query("DELETE FROM locations")
//    suspend fun deleteAll() : Int
//
//    @Query("SELECT * FROM locations")
//    fun getAllLocations() : List<Location>
//
//
//    //suspend fun insertLocations(locations: List<Location>) : List<Long>
//
//}