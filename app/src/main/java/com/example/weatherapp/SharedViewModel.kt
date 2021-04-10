package com.example.weatherapp

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.adapters.SearchRecycleAdapter
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.model.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SharedViewModel: ViewModel() {

    var locations = MutableLiveData<List<Location>>()

    init {
        locations.value = listOf()
    }

    fun retrieveLocations(searchQuery: String) {

        viewModelScope.launch {

            try {
                //get all Locations from search
                val responses = RetrofitInstance.api.getLocations(searchQuery)

                //use woeids from found Locations to fill every location's consolidated_weather list of Weathers
                for (loc: Location in responses) {
                    var response = RetrofitInstance.api.getLocation(loc.woeid)
                    loc.consolidated_weather = response.consolidated_weather
                }

                locations.value = responses

            }
            catch (e: Exception) {
    //            withContext(Dispatchers.Main) {
    //                Toast.makeText(, "Please check your Internet connection", Toast.LENGTH_LONG)
    //                    .show()
            }
        }

    }

}