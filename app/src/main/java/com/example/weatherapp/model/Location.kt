package com.example.weatherapp.model

data class Location (
    var woeid: Integer,
    var title: String,
    var consolidated_weather: List<Weather>
)