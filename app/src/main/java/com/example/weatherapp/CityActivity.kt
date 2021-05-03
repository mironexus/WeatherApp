package com.example.weatherapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.weatherapp.adapters.IncrementalRecyclerAdapter
import com.example.weatherapp.adapters.SearchRecycleAdapter
import com.example.weatherapp.databinding.ActivityCityBinding
import com.example.weatherapp.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

private lateinit var binding: ActivityCityBinding

class CityActivity : AppCompatActivity() {

    private val cityItemViewModel: CityItemViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val woeid = intent.getIntExtra("woeid", 851128)
        cityItemViewModel.setLocation(woeid)
        var isMyCity = intent.getBooleanExtra("isMyCity", false)

        if (isMyCity) {
            binding.setMyCity.setImageResource(R.drawable.ic_star_1)
        }
        else {
            binding.setMyCity.setImageResource(R.drawable.ic_star_0)
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.setMyCity.setOnClickListener {

            if (isMyCity) {
                binding.setMyCity.setImageResource(R.drawable.ic_star_0)
                cityItemViewModel.removeFromMyCities(woeid)
                isMyCity = false
            }
            else {
                binding.setMyCity.setImageResource(R.drawable.ic_star_1)
                cityItemViewModel.setAsMyCity(woeid)
                isMyCity = true
            }

        }


        binding.refreshLayout.setOnRefreshListener {
            cityItemViewModel.setLocation(woeid)
            binding.refreshLayout.isRefreshing = false
        }


        cityItemViewModel.location.observe(this, Observer {
            //set data of the views

            binding.collapsingToolbar.title = cityItemViewModel.location.value?.title

            val zoneId = ZoneId.of(cityItemViewModel.location.value?.timezone)
            val timeWithZone = LocalDateTime.now(zoneId)
            val formatter = DateTimeFormatter.ofPattern("E, MMMM dd")
            binding.date.text = formatter.format(timeWithZone)

            val tz = TimeZone.getTimeZone(cityItemViewModel.location.value?.timezone)
            val shortTZ = tz.getDisplayName(false, TimeZone.SHORT)
            val timezone = shortTZ
            binding.time.text = timeWithZone.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + " (" + timezone + ")"

            binding.weatherDescription.text = cityItemViewModel.location.value?.consolidated_weather?.get(0)?.weather_state_name
            binding.tempLabel.text = cityItemViewModel.location.value?.consolidated_weather?.get(0)?.the_temp?.toInt().toString() + "°"
            val image = cityItemViewModel.location.value?.consolidated_weather?.get(0)?.weather_state_abbr
            binding.icon.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")


            binding.minMax.setValue(
                cityItemViewModel.location.value?.consolidated_weather?.get(0)?.min_temp?.toInt().toString()
                + "° / "
                + cityItemViewModel.location.value?.consolidated_weather?.get(0)?.max_temp?.toInt().toString()
                + "°"
            )

            binding.wind.setValue(
                cityItemViewModel.location.value?.consolidated_weather?.get(0)?.wind_speed?.toInt().toString()
                + " km/h " + "(" + cityItemViewModel.location.value?.consolidated_weather?.get(0)?.wind_direction_compass + ")"
            )

            binding.humidity.setValue(
                cityItemViewModel.location.value?.consolidated_weather?.get(0)?.humidity.toString() + "%"
            )

            binding.pressure.setValue(
                cityItemViewModel.location.value?.consolidated_weather?.get(0)?.air_pressure?.toInt().toString() + " hPa"
            )

            binding.visibility.setValue(
                cityItemViewModel.location.value?.consolidated_weather?.get(0)?.visibility?.toInt().toString() + " km"
            )

            binding.accuracy.setValue(
                cityItemViewModel.location.value?.consolidated_weather?.get(0)?.predictability.toString() + "%"
            )

        })


        //hourly forecast
        cityItemViewModel.getWeathersOnDate(woeid)
        var hourlyAdapter = IncrementalRecyclerAdapter(cityItemViewModel.weatherListDate, true)
        binding.hourlyRecyclerView.adapter = hourlyAdapter
        val layoutManagerHourly = LinearLayoutManager(applicationContext)
        layoutManagerHourly.orientation = LinearLayoutManager.HORIZONTAL
        binding.hourlyRecyclerView.layoutManager = layoutManagerHourly

        cityItemViewModel.weatherListDate.observe(this, Observer {
            hourlyAdapter.updateData(cityItemViewModel.weatherListDate)
        })


        //daily forecast
        var dailyAdapter = IncrementalRecyclerAdapter(cityItemViewModel.weatherList, false)
        binding.dailyRecyclerView.adapter = dailyAdapter
        val layoutManagerDaily = LinearLayoutManager(applicationContext)
        layoutManagerDaily.orientation = LinearLayoutManager.HORIZONTAL
        binding.dailyRecyclerView.layoutManager = layoutManagerDaily

        cityItemViewModel.weatherList.observe(this, Observer {
            dailyAdapter.updateData(cityItemViewModel.weatherList)
        })

    }
}