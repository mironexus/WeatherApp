package com.example.weatherapp.model

import java.io.Serializable

class LocationDetails (
    var woeid: Int,
    var title: String,
    var consolidated_weather: List<Weather>
) : Serializable