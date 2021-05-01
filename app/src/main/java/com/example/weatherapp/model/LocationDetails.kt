package com.example.weatherapp.model

import java.io.Serializable
import java.util.*

class LocationDetails (
    var woeid: Int,
    var title: String,
    var consolidated_weather: List<Weather>,
    var time: Date,
    var timezone: String
) : Serializable