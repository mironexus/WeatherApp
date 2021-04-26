package com.example.weatherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Location
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.launch


class SharedViewModel: ViewModel() {

    var locations = MutableLiveData<List<Location>>()
    val repository = Repository()

    init {
        locations.value = listOf()
    }

    fun retrieveLocations(searchQuery: String) {
        viewModelScope.launch {
            locations.value = repository.getLocations(searchQuery)
        }
    }

}