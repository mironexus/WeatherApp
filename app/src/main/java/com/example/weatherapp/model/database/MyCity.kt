package com.example.weatherapp.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "my_cities")
data class MyCity (
    @PrimaryKey
    var woeid: Int
) : Serializable