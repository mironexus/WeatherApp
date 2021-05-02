package com.example.weatherapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.CityActivity
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationCard
import com.example.weatherapp.model.Weather
import kotlinx.android.synthetic.main.incremental_recycler_item.view.*
import kotlinx.android.synthetic.main.search_recycler_item.view.*
import org.w3c.dom.Text

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


    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {
        val currentItem = weatherList.value?.get(position)

        if (currentItem != null) {

            if (hourly){
                holder.time.text = currentItem.wind_direction_compass + " H"
                holder.temperature.text = currentItem.the_temp.toInt().toString() + "°"
            }
            else {
                holder.time.text = currentItem.air_pressure.toString() + " D"
                holder.temperature.text = currentItem.the_temp.toInt().toString() + "°"
            }


            val image = currentItem.weather_state_abbr
            holder.weatherStateAbbr.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")
        }
    }

}