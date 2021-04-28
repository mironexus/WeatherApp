package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "search_locations")
data class SearchLocation (
    var timestamp: Long,
    @PrimaryKey
    var woeid: Int,
    var title: String,
    var latt_long: String
) : Serializable