package com.example.weatherapp.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.LocationCard
import com.example.weatherapp.model.repository.RepositoryImpl
import kotlinx.coroutines.launch


class SharedViewModel(application: Application): AndroidViewModel(application) {

    var locations = MutableLiveData<List<LocationCard>>()
    var myCities = MutableLiveData<List<LocationCard>>()
    var suggestions = MutableLiveData<ArrayList<String>>()

    private val repository: RepositoryImpl

    init {
        locations.value = listOf()
        myCities.value = listOf()
        suggestions.value = arrayListOf<String>()
        suggestions.value!!.add("Zagreb")
        suggestions.value!!.add("London")
        repository = RepositoryImpl(getApplication())
    }

    fun getSearchSuggestionList(query: String) {
        viewModelScope.launch {
            suggestions.value = repository.getSearchSuggestionList(query)
        }
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
            retrieveMyCities()
        }
    }

    fun removeFromMyCities(woeid: Int) {
        viewModelScope.launch {
            repository.removeFromMyCites(woeid)
            retrieveLocations()
            retrieveMyCities()
        }
    }



}