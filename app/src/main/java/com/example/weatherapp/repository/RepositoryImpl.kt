package com.example.weatherapp.repository

import android.app.Application
import android.util.Log
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.database.AppDatabase
import com.example.weatherapp.database.MyCitiesDAO
import com.example.weatherapp.database.SearchDAO
import com.example.weatherapp.model.*

class RepositoryImpl(application: Application) {

    private val searchDao : SearchDAO
    private val myCitiesDAO: MyCitiesDAO
    val database = AppDatabase.getInstance(application)

    init {
        searchDao = database.searchDAO
        myCitiesDAO = database.myCitiesDAO
    }

    private suspend fun getAllLocations(): List<SearchLocation> {
        return searchDao.getAllSearchLocations()
    }


    suspend fun saveSearchResult(query: String) {
        //make request for locations defined by searchQuery
        val locationListResponse = RetrofitInstance.api.getLocationsFromApi(query)
        var fetchedLocations: List<SearchLocation>

        if(locationListResponse.isSuccessful) {
            //get all Locations from search
            fetchedLocations = locationListResponse.body()!!

            //save searchLocation from list into database one by one
            for (fetchedLocation: SearchLocation in fetchedLocations) {
                fetchedLocation.timestamp = System.currentTimeMillis()
                searchDao.insertSearchLocation(fetchedLocation)
            }
        }
        else {
            Log.e("RETROFIT_ERROR", locationListResponse.code().toString())
        }

    }



    suspend fun getLocationCardList(): List<LocationCard> {

        //to be returned from function
        var locationCardList: MutableList<LocationCard> = mutableListOf()

        var locationsFromDatabase = getAllLocations()

        for (location: SearchLocation in locationsFromDatabase) {

            var check = checkIfMyCity(location.woeid)

            var locationCard = LocationCard(
                location.title,
                location.woeid,
                "lt",
                "ln",
                10.0,
                "sn",
                check
            )
            locationCardList.add(locationCard)

        }


        return locationCardList
    }

    suspend fun deleteAllSearchLocations() {
        searchDao.deleteAllSearchLocations()
    }


    suspend fun getSingleLocation(woeid: Int): LocationDetails {
        val emptyWeatherList: List<Weather> = listOf()
        var location = LocationDetails(1,"", emptyWeatherList)

        val singleLocationResponse = RetrofitInstance.api.getSingleLocationFromApi(woeid)
        if(singleLocationResponse.isSuccessful) {

            location = singleLocationResponse.body()!!
            return location

        }
        else {
            Log.e("RETROFIT_ERROR", singleLocationResponse.code().toString())
        }
        return location
    }

    suspend fun setAsMyCity(woeid: Int) {
        var myCity = MyCity(woeid)
        myCitiesDAO.insertMyCitiesLocation(myCity)
    }

    suspend fun checkIfMyCity(woeid: Int): Boolean {
        return myCitiesDAO.checkIfMyCity(woeid)
    }


}