package com.example.weatherapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.database.AppDatabase
import com.example.weatherapp.model.LocationCard
import com.example.weatherapp.model.SearchLocation
import com.example.weatherapp.repository.RepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class SharedViewModel(application: Application): AndroidViewModel(application) {

    var locations = MutableLiveData<List<LocationCard>>()
    var myCities = MutableLiveData<List<LocationCard>>()

    private val repository: RepositoryImpl

    init {
        locations.value = listOf()
        myCities.value = listOf()
        repository = RepositoryImpl(getApplication())
    }


    fun saveLocations(query: String) {
        viewModelScope.launch {
            repository.saveSearchResult(query)
            retrieveLocations()
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAllSearchLocations()
            retrieveLocations()
        }
    }

    fun retrieveLocations() {
        viewModelScope.launch {
            locations.value = repository.getLocationCardList(false)
        }
    }

    fun retrieveMyCities() {
        viewModelScope.launch {
            myCities.value = repository.getLocationCardList(true)
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