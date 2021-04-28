package com.example.weatherapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.LocationDetails
import com.example.weatherapp.repository.RepositoryImpl
import kotlinx.coroutines.launch

class CityItemViewModel(application: Application): AndroidViewModel(application) {

    var location = MutableLiveData<LocationDetails>()
    private val repository: RepositoryImpl

    init {
        repository = RepositoryImpl(getApplication())
    }

    fun setLocation(woeid: Int) {
        viewModelScope.launch {
            location.value = repository.getSingleLocation(woeid)
        }
    }

}