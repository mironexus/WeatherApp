package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.MyCity

@Dao
interface MyCitiesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMyCitiesLocation(myCity: MyCity)

    @Query("SELECT * FROM my_cities")
    suspend fun getAllMyCities() : List<MyCity>

    @Query("DELETE FROM my_cities")
    suspend fun deleteAllMyCities()

    @Query("SELECT EXISTS(SELECT * FROM my_cities WHERE woeid = :woeid)")
    suspend fun checkIfMyCity(woeid: Int) : Boolean

//    @Query("SELECT * FROM my_cities WHERE woeid = :woeid")
//    suspend fun checkIfMyCity(woeid: Int)

}