package com.example.weatherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.Location
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.launch

class CityItemViewModel: ViewModel() {

    var location = MutableLiveData<Location>()
    val repository = Repository()

    init {

    }

    fun setLocation(woeid: Int) {
        viewModelScope.launch {
            location.value = repository.getLocation(woeid)
        }
    }

}