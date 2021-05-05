package com.example.weatherapp.model.repository

import android.app.Application
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weatherapp.model.api.RetrofitInstance
import com.example.weatherapp.model.database.*
import com.example.weatherapp.model.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList

class RepositoryImpl(application: Application) {

    private val searchDao : SearchDAO
    private val myCitiesDAO: MyCitiesDAO
    val database = AppDatabase.getInstance(application)

    init {
        searchDao = database.searchDAO
        myCitiesDAO = database.myCitiesDAO
    }

    private suspend fun getAllLocations(): MutableList<SearchLocation> {
        return searchDao.getAllSearchLocations()
    }

    private suspend fun getAllMyCitiesWoeids(): List<Int> {
        return myCitiesDAO.getAllMyCitiesWoeids()
    }

    private suspend fun getAllSearchWoeids(): List<Int> {
        return searchDao.getAllSearchWoeids()
    }

    suspend fun getSearchSuggestionList(query: String): ArrayList<String> {
        val locationListResponse = RetrofitInstance.api.getLocationsFromApi(query)
        val fetchedLocations: List<SearchLocation>

        var woeidList: ArrayList<String> = arrayListOf<String>()

        if(locationListResponse.isSuccessful) {
            //get all Locations from search
            fetchedLocations = locationListResponse.body()!!

            //save searchLocation titles from list into list that is being returned one by one
            for (fetchedLocation: SearchLocation in fetchedLocations) {
                    woeidList.add(fetchedLocation.title)
            }
        }
        else {
            Log.e("RETROFIT_ERROR", locationListResponse.code().toString())
        }

        return woeidList
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getLocationCardList(isMyCityList: Boolean): List<LocationCard> {

        //to be returned from function
        val locationCardList: MutableList<LocationCard> = mutableListOf()

        var locations: MutableList<LocationDetails> = mutableListOf()

        var woeids: List<Int>

        if (isMyCityList) {
            //get every woeid from my_cities table
            woeids = getAllMyCitiesWoeids()
            for(woeid in woeids) {
                //for every woeid fetch SearchLocation with that woeid
                locations.add(getSingleLocation(woeid))
            }
        }
        else {
            woeids = getAllSearchWoeids()
            for(woeid in woeids) {
                locations.add(getSingleLocation(woeid))
            }
        }

        for (location: LocationDetails in locations) {

            val coordinates = getLattLongFloat(location.latt_long)
            val coordinatesString = getLattLong(coordinates)
            val distance = getDistance(coordinates).toInt() / 1000
            val check = checkIfMyCity(location.woeid)

            var time = ""
            var timezone = ""

            if(isMyCityList) {
                val zoneId = ZoneId.of(location.timezone)
                val timeWithZone = LocalDateTime.now(zoneId)
                time = timeWithZone.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

                val tz = TimeZone.getTimeZone(location.timezone)
                val shortTZ = tz.getDisplayName(false, TimeZone.SHORT)
                timezone = shortTZ
            }

            val locationCard = LocationCard(
                location.title,
                location.woeid,
                coordinatesString[0],
                coordinatesString[1],
                location.consolidated_weather[0].the_temp,
                location.consolidated_weather[0].weather_state_abbr,
                distance,
                time,
                timezone,
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
        var date: Date = Date()
        var location = LocationDetails(1,"", emptyWeatherList, date, "", "")

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
        val separateLattLong = lattLongString.split(",")
        val lattLongFloat: MutableList<Float> = mutableListOf()

        for(l in separateLattLong) {
            lattLongFloat.add(l.toFloat())
        }
        return lattLongFloat
    }

    private fun getLattLong(lattLongFloat: MutableList<Float>): MutableList<String> {

        val lattFloat = lattLongFloat[0]
        val longFloat = lattLongFloat[1]

        var lattString = ""
        var longString = ""
        val compassValues: MutableList<String> = mutableListOf()

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

    private fun getDistance(coordinates: MutableList<Float>): Float {

        val start = Location("Start")
        start.latitude = 45.807259
        start.longitude = 15.967600

        val destination = Location("Destination")
        destination.latitude = coordinates[0].toDouble()
        destination.longitude = coordinates[1].toDouble()

        return start.distanceTo(destination)
    }

    suspend fun setAsMyCity(woeid: Int) {
        val myCity = MyCity(woeid)
        myCitiesDAO.insertMyCitiesLocation(myCity)
    }

    suspend fun checkIfMyCity(woeid: Int): Boolean {
        return myCitiesDAO.checkIfMyCity(woeid)
    }

    suspend fun removeFromMyCites(woeid: Int) {
        myCitiesDAO.removeFromMyCities(woeid)
    }

    suspend fun getWeathersOnDate(woeid: Int, year: Int, month: Int, day: Int): List<Weather> {
        //make request for locations defined by searchQuery
        val weatherListResponse = RetrofitInstance.api.getSingleLocationFromApiDate(woeid, year, month, day)
        var fetchedWeathers: List<Weather> = listOf()

        if(weatherListResponse.isSuccessful) {
            //get all Locations from search
            fetchedWeathers = weatherListResponse.body()!!
            //take only first ten items from list
            fetchedWeathers = fetchedWeathers.subList(0, 10)
        }
        else {
            Log.e("RETROFIT_ERROR", weatherListResponse.code().toString())
        }

        return fetchedWeathers

    }


}