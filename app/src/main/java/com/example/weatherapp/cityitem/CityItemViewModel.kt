package com.example.weatherapp.cityitem

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.LocationDetails
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.repository.RepositoryImpl
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CityItemViewModel(application: Application): AndroidViewModel(application) {

    var location = MutableLiveData<LocationDetails>()
    var weatherList = MutableLiveData<List<Weather>>()
    var weatherListDate = MutableLiveData<List<Weather>>()
    private val repository: RepositoryImpl

    init {
        weatherList.value = listOf()
        weatherListDate.value = listOf()
        repository = RepositoryImpl(getApplication())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setLocation(woeid: Int) {
        viewModelScope.launch {
            location.value = repository.getSingleLocation(woeid)
            
            //needs to be updated here because daily forecast uses this list and this list is fetched within the fetched location
            weatherList.value = location.value!!.consolidated_weather

            //to refresh everything at once, mainly because of refresh layout
            getWeathersOnDate(woeid)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeathersOnDate(woeid: Int) {
        viewModelScope.launch {
            val current = LocalDateTime.now()
            val day = current.dayOfMonth
            val month = current.monthValue
            val year = current.year
            weatherListDate.value = repository.getWeathersOnDate(woeid, year, month, day)
        }
    }

    fun setAsMyCity(woeid: Int) {
        viewModelScope.launch {
            repository.setAsMyCity(woeid)
        }
    }

    fun removeFromMyCities(woeid: Int) {
        viewModelScope.launch {
            repository.removeFromMyCites(woeid)
        }
    }

}