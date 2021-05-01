package com.example.weatherapp.repository

import android.app.Application
import android.location.Location
import android.util.Log
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.database.AppDatabase
import com.example.weatherapp.database.MyCitiesDAO
import com.example.weatherapp.database.SearchDAO
import com.example.weatherapp.model.*
import java.util.*

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
        val fetchedLocations: List<SearchLocation>

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



    suspend fun getLocationCardList(isMyCityList: Boolean): List<LocationCard> {

        //to be returned from function
        val locationCardList: MutableList<LocationCard> = mutableListOf()

        val locationsFromDatabase = getAllLocations()

        for (location: SearchLocation in locationsFromDatabase) {


            val check = checkIfMyCity(location.woeid)
            val singleLocation = getSingleLocation(location.woeid)
            val weather = getSingleWeather(singleLocation)
            val coordinates = getLattLongFloat(location.latt_long)
            val coordinatesString = getLattLong(coordinates)
            val distance = getDistance(coordinates).toInt() / 1000

            var time = ""
            var timezone = ""

            if(isMyCityList) {
                var tz = TimeZone.getTimeZone(singleLocation.timezone)
                var shortTZ = tz.getDisplayName(false, TimeZone.SHORT)
                timezone = shortTZ
                time = singleLocation.time.time.toString()
            }

            val locationCard = LocationCard(
                location.title,
                location.woeid,
                coordinatesString[0],
                coordinatesString[1],
                weather.the_temp,
                weather.weather_state_abbr,
                distance,
                time,
                timezone,
                check
            )
            //to put in myCities list
            if(check && isMyCityList) {
                locationCardList.add(locationCard)
            }
            //to filter non-MyCities from myCities list
            else if(!check && isMyCityList) {

            }
            else {
                locationCardList.add(locationCard)
            }
        }


        return locationCardList
    }

    suspend fun deleteAllSearchLocations() {
        searchDao.deleteAllSearchLocations()
    }


    suspend fun getSingleLocation(woeid: Int): LocationDetails {
        val emptyWeatherList: List<Weather> = listOf()
        var date: Date = Date()
        var location = LocationDetails(1,"", emptyWeatherList, date, "")

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

    private fun getSingleWeather(singleLocation: LocationDetails): Weather {
        return singleLocation.consolidated_weather[0]
    }

    private fun getLattLongFloat(lattLongString: String): MutableList<Float> {
        var separateLattLong = lattLongString.split(",")
        var lattLongFloat: MutableList<Float> = mutableListOf()

        for(l in separateLattLong) {
            lattLongFloat.add(l.toFloat())
        }
        return lattLongFloat
    }

    private fun getLattLong(lattLongFloat: MutableList<Float>): MutableList<String> {

        var lattFloat = lattLongFloat[0]
        var longFloat = lattLongFloat[1]

        var lattString = ""
        var longString = ""
        var compassValues: MutableList<String> = mutableListOf()

        var lattInt = lattFloat.toInt()
        var lattRemainder = lattFloat - lattInt
        var formattedlattRemainder = String.format("%.2f", lattRemainder).replace("0.", "")

        if(lattFloat > 0) {
            lattString = "$lattInt°$formattedlattRemainder'N"
        }
        else{
            lattInt = -lattInt
            lattRemainder = -lattRemainder
            formattedlattRemainder = String.format("%.2f", lattRemainder).replace("0.", "")
            lattString = "$lattInt°$formattedlattRemainder'S"
        }

        compassValues.add(lattString)

        var longInt = longFloat.toInt()
        var longRemainder = longFloat - longInt
        var formattedLongRemainder = String.format("%.2f", longRemainder).replace("0.", "")

        if(longFloat > 0) {
            longString = "$longInt°$formattedLongRemainder'E"
        }
        else{
            longInt = -longInt
            longRemainder = -longRemainder
            formattedLongRemainder = String.format("%.2f", longRemainder).replace("0.", "")
            longString = "$longInt°$formattedLongRemainder'W"
        }

        compassValues.add(longString)

        return compassValues

    }

    fun getDistance(coordinates: MutableList<Float>): Float {

        val start = Location("Start")
        start.latitude = 45.807259
        start.longitude = 15.967600

        val destination = Location("Destination")
        destination.latitude = coordinates[0].toDouble()
        destination.longitude = coordinates[1].toDouble()

        val distance = start.distanceTo(destination)
        return distance
    }

    suspend fun setAsMyCity(woeid: Int) {
        val myCity = MyCity(woeid)
        myCitiesDAO.insertMyCitiesLocation(myCity)
    }

    private suspend fun checkIfMyCity(woeid: Int): Boolean {
        return myCitiesDAO.checkIfMyCity(woeid)
    }

    suspend fun removeFromMyCites(woeid: Int) {
        myCitiesDAO.removeFromMyCities(woeid)
    }


}