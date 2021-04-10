package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location (
    var woeid: Integer,
    var title: String,
    var consolidated_weather: List<Weather>
) : Serializable