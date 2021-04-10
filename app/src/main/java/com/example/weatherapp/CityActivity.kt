package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import coil.load
import com.example.weatherapp.databinding.ActivityCityBinding
import com.example.weatherapp.model.Weather

private lateinit var binding: ActivityCityBinding

class CityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //setting title of the Toolbar
        val title = intent.getStringExtra("title")
        val woeid = intent.getStringExtra("woeid")
        val consolidated_weather: List<Weather> = intent.extras?.get("consolidated_weather") as List<Weather>



        val image = consolidated_weather[0].weather_state_abbr
        binding.imageView.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")

        binding.title.text = title
        binding.date.text = consolidated_weather[0].applicable_date.toString()
        binding.theTemp.text = consolidated_weather[0].the_temp.toString()
        binding.visibility.text = consolidated_weather[0].visibility.toString()
        binding.wind.text = consolidated_weather[0].wind_direction_compass

        //binding.collapsingToolbar.title = "$name $surname"


    }
}