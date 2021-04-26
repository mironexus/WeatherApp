package com.example.weatherapp.repository

import android.util.Log
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.Weather

class Repository {

    suspend fun getLocation(woeid: Int): Location {

        val emptyWeatherList: List<Weather> = listOf()
        var location = Location(1,"", emptyWeatherList)

        val singleLocationResponse = RetrofitInstance.api.getLocation(woeid)
        if(singleLocationResponse.isSuccessful) {

            location = singleLocationResponse.body()!!
            return location

        }
        else {
            Log.e("RETROFIT_ERROR", singleLocationResponse.code().toString())
        }
        return location
    }

    suspend fun getLocations(searchQuery: String): List<Location> {

        //make request for locations defined by searchQuery
        val locationListResponse = RetrofitInstance.api.getLocations(searchQuery)
        var fetchedLocations: List<Location> = listOf()

        if(locationListResponse.isSuccessful) {
            //get all Locations from search
            fetchedLocations = locationListResponse.body()!!

            for (fetchedLocation: Location in fetchedLocations) {
                //use woeids from found Locations to fill every location's consolidated_weather list of Weathers
                val singleLocation = getLocation(fetchedLocation.woeid)
                fetchedLocation.consolidated_weather = singleLocation.consolidated_weather
            }
            return fetchedLocations
        }
        else {
            Log.e("RETROFIT_ERROR", locationListResponse.code().toString())
        }
        return fetchedLocations
    }

}