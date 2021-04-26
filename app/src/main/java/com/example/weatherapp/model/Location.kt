package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location (
    var woeid: Int,
    var title: String,
    var consolidated_weather: List<Weather>
) : Serializable