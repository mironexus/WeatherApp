package com.example.weatherapp.cityitem

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityCityBinding
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
        var isMyCity = intent.getBooleanExtra("isMyCity", false)

        binding.backArrow.setOnClickListener {
            finish()
        }

        if(isNetworkConnected()) {
            cityItemViewModel.setLocation(woeid)

            binding.refreshLayout.setOnRefreshListener {
                cityItemViewModel.setLocation(woeid)
                binding.refreshLayout.isRefreshing = false
            }

            if (isMyCity) {
                binding.setMyCity.setImageResource(
                    R.drawable.ic_star_1
                )
            } else {
                binding.setMyCity.setImageResource(
                    R.drawable.ic_star_0
                )
            }

            binding.setMyCity.setOnClickListener {

                if (isMyCity) {
                    binding.setMyCity.setImageResource(
                        R.drawable.ic_star_0
                    )
                    cityItemViewModel.removeFromMyCities(woeid)
                    isMyCity = false
                } else {
                    binding.setMyCity.setImageResource(
                        R.drawable.ic_star_1
                    )
                    cityItemViewModel.setAsMyCity(woeid)
                    isMyCity = true
                }

            }


            //set data of the views
            cityItemViewModel.location.observe(this, Observer {

                binding.collapsingToolbar.title = cityItemViewModel.location.value?.title

                val zoneId = ZoneId.of(cityItemViewModel.location.value?.timezone)
                val timeWithZone = LocalDateTime.now(zoneId)
                val formatter = DateTimeFormatter.ofPattern("E, MMMM dd")
                binding.date.text = formatter.format(timeWithZone)

                val tz = TimeZone.getTimeZone(cityItemViewModel.location.value?.timezone)
                val shortTZ = tz.getDisplayName(false, TimeZone.SHORT)
                val timezone = shortTZ
                binding.time.text =
                    timeWithZone.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + " (" + timezone + ")"

                binding.weatherDescription.text =
                    cityItemViewModel.location.value?.consolidated_weather?.get(0)?.weather_state_name
                binding.tempLabel.text =
                    cityItemViewModel.location.value?.consolidated_weather?.get(0)?.the_temp?.toInt()
                        .toString() + "°"
                val image =
                    cityItemViewModel.location.value?.consolidated_weather?.get(0)?.weather_state_abbr
                binding.icon.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")


                binding.minMax.setValue(
                    cityItemViewModel.location.value?.consolidated_weather?.get(0)?.min_temp?.toInt()
                        .toString()
                            + "° / "
                            + cityItemViewModel.location.value?.consolidated_weather?.get(0)?.max_temp?.toInt()
                        .toString()
                            + "°"
                )

                binding.wind.setValue(
                    cityItemViewModel.location.value?.consolidated_weather?.get(0)?.wind_speed?.toInt()
                        .toString()
                            + " km/h " + "(" + cityItemViewModel.location.value?.consolidated_weather?.get(
                        0
                    )?.wind_direction_compass + ")"
                )

                binding.humidity.setValue(
                    cityItemViewModel.location.value?.consolidated_weather?.get(0)?.humidity.toString() + "%"
                )

                binding.pressure.setValue(
                    cityItemViewModel.location.value?.consolidated_weather?.get(0)?.air_pressure?.toInt()
                        .toString() + " hPa"
                )

                binding.visibility.setValue(
                    cityItemViewModel.location.value?.consolidated_weather?.get(0)?.visibility?.toInt()
                        .toString() + " km"
                )

                binding.accuracy.setValue(
                    cityItemViewModel.location.value?.consolidated_weather?.get(0)?.predictability.toString() + "%"
                )

            })


            //hourly forecast
            cityItemViewModel.getWeathersOnDate(woeid)
            var hourlyAdapter =
                IncrementalRecyclerAdapter(
                    cityItemViewModel.weatherListDate,
                    true
                )
            binding.hourlyRecyclerView.adapter = hourlyAdapter
            val layoutManagerHourly = LinearLayoutManager(applicationContext)
            layoutManagerHourly.orientation = LinearLayoutManager.HORIZONTAL
            binding.hourlyRecyclerView.layoutManager = layoutManagerHourly

            cityItemViewModel.weatherListDate.observe(this, Observer {
                hourlyAdapter.updateData(cityItemViewModel.weatherListDate)
            })


            //daily forecast
            var dailyAdapter =
                IncrementalRecyclerAdapter(
                    cityItemViewModel.weatherList,
                    false
                )
            binding.dailyRecyclerView.adapter = dailyAdapter
            val layoutManagerDaily = LinearLayoutManager(applicationContext)
            layoutManagerDaily.orientation = LinearLayoutManager.HORIZONTAL
            binding.dailyRecyclerView.layoutManager = layoutManagerDaily

            cityItemViewModel.weatherList.observe(this, Observer {
                dailyAdapter.updateData(cityItemViewModel.weatherList)
            })
        }

    }

    private fun isNetworkConnected(): Boolean {
        //1
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //2
        val activeNetwork = connectivityManager.activeNetwork
        //3
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        //4
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


}