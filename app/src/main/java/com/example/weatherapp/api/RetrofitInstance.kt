package com.example.weatherapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val interceptor = HttpLoggingInterceptor()
    private val client: OkHttpClient

    init {
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    val BASE_URL = "https://www.metaweather.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: MetaWeatherApi by lazy {
        retrofit.create(MetaWeatherApi::class.java)
    }

}