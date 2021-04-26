package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import coil.load
import com.example.weatherapp.databinding.ActivityCityBinding

private lateinit var binding: ActivityCityBinding

class CityActivity : AppCompatActivity() {

    private val cityItemViewModel: CityItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val woeid = intent.getIntExtra("woeid", 851128)
        cityItemViewModel.setLocation(woeid)

        cityItemViewModel.location.observe(this, Observer {
            //set data of the views
            val image = cityItemViewModel.location.value?.consolidated_weather?.get(0)?.weather_state_abbr
            binding.imageView.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")
            binding.title.text = cityItemViewModel.location.value?.title
            binding.date.text = cityItemViewModel.location.value?.consolidated_weather?.get(0)?.applicable_date.toString()
            binding.theTemp.text = cityItemViewModel.location.value?.consolidated_weather?.get(0)?.the_temp.toString()
            binding.visibility.text = cityItemViewModel.location.value?.consolidated_weather?.get(0)?.visibility.toString()
            binding.wind.text = cityItemViewModel.location.value?.consolidated_weather?.get(0)?.wind_direction_compass
        })




    }
}