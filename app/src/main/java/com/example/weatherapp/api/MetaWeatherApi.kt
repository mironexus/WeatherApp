package com.example.weatherapp.api

import com.example.weatherapp.model.Location
import com.example.weatherapp.model.Weather
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MetaWeatherApi {

    @GET("api/location/{woeid}")
    suspend fun getLocation(@Path("woeid") woeid: Integer): Location


    //    http://service.com/movies/list?movie_lang=eng
    //    @GET("http://service.com/movies/list")
    //    fun getMovieList(@Query("movie_lang") userLanguage: String?):
    @GET("/api/location/search/")
    suspend fun getLocations(@Query("query") searchString: String): List<Location>



}