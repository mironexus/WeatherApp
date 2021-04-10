package com.example.weatherapp.model

import java.io.Serializable
import java.util.*

data class Weather(
    var id: Long,
    var weather_state_name: String,
    var weather_state_abbr: String,
    var wind_direction_compass: String,
    var created: Date,
    var applicable_date: Date,
    var min_temp: Double,
    var max_temp: Double,
    var the_temp: Double,
    var wind_speed: Double,
    var wind_direction: Double,
    var air_pressure: Float,
    var humidity: Integer,
    var visibility: Double,
    var predictability: Integer
) : Serializable