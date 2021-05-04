package com.example.weatherapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.R
import com.example.weatherapp.model.Weather
import kotlinx.android.synthetic.main.incremental_recycler_item.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class IncrementalRecyclerAdapter(
    private var weatherList: MutableLiveData<List<Weather>>,
    private var hourly: Boolean
) : RecyclerView.Adapter<IncrementalRecyclerAdapter.RecycleViewHolder>() {

    inner class RecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.time
        val weatherStateAbbr: ImageView = itemView.icon
        val temperature: TextView = itemView.temp
    }

    fun updateData(weatherList: MutableLiveData<List<Weather>>) {
        this.weatherList = weatherList

        //find better notify method
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.incremental_recycler_item,
            parent, false)
        return RecycleViewHolder(
            itemView
        )
    }

    override fun getItemCount() = weatherList.value!!.size


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        val currentItem = weatherList.value?.get(position)

        var formatter: DateTimeFormatter
        var dateTime: LocalDateTime

        if (currentItem != null) {

            if (hourly){
                // 07:00 created
                formatter = DateTimeFormatter.ofPattern("HH")
                var dateTimeWithoutZ = currentItem?.created.replace("Z", "")
                dateTime = LocalDateTime.parse(dateTimeWithoutZ)

                holder.time.text = formatter.format(dateTime) + ":00"
                holder.temperature.text = currentItem.the_temp.toInt().toString() + "°"
            }
            else {
                // TUE applicable date
                val dateWithDashesFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                var date = LocalDate.parse(currentItem?.applicable_date, dateWithDashesFormatter)
                formatter = DateTimeFormatter.ofPattern("E")
                holder.time.text = formatter.format(date).toUpperCase(Locale.getDefault())
                holder.temperature.text = currentItem.the_temp.toInt().toString() + "°"
            }


            val image = currentItem.weather_state_abbr
            holder.weatherStateAbbr.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")
        }
    }

}