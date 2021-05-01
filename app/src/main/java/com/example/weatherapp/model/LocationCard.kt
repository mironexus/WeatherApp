package com.example.weatherapp.model

data class LocationCard (
    var title: String,
    var woeid: Int,
    var lattitude: String,
    var longitude: String,
    var the_temp: Double,
    var weather_state_abbr: String,
    var distance: Int,
    var time: String,
    var timezone: String,
    var isMyCity: Boolean
)