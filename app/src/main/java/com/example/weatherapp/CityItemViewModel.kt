package com.example.weatherapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.LocationDetails
import com.example.weatherapp.model.Weather
import com.example.weatherapp.repository.RepositoryImpl
import kotlinx.coroutines.launch

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

    fun setLocation(woeid: Int) {
        viewModelScope.launch {
            location.value = repository.getSingleLocation(woeid)
            
            //needs to be updated here because daily forecast uses this list and this list is fetched within the fetched location
            weatherList.value = location.value!!.consolidated_weather
        }
    }

    fun getWeathersOnDate(woeid: Int, year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            weatherListDate.value = repository.getWeathersOnDate(woeid, year, month, day)
        }
    }

}