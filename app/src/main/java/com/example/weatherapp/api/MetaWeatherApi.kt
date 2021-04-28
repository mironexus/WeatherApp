package com.example.weatherapp.api

import com.example.weatherapp.model.LocationDetails
import com.example.weatherapp.model.SearchLocation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface MetaWeatherApi {

    //    http://service.com/movies/list?movie_lang=eng
    //    @GET("http://service.com/movies/list")
    //    fun getMovieList(@Query("movie_lang") userLanguage: String?):
    //1
    @GET("/api/location/search/")
    suspend fun getLocationsFromApi(@Query("query") query: String): Response<List<SearchLocation>>

    //2
    @GET("api/location/{woeid}")
    suspend fun getSingleLocationFromApi(@Path("woeid") woeid: Int): Response<LocationDetails>

    //3
    @GET("api/location/{woeid}/{year}/{month}/{day}")
    suspend fun getSingleLocationFromApiDate(@Path("woeid") woeid: Int,
                                             @Path("year") year: Int,
                                             @Path("month") month: Int,
                                             @Path("day") day: Int): Response<LocationDetails>

    //4
    @GET("/api/location/search/")
    suspend fun getLocationsFromApiLattLong(@Query("lattlong") lattlong: String): Response<List<SearchLocation>>



}