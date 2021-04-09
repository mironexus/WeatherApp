package com.example.weatherapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.weatherapp.R
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.Weather
import kotlinx.android.synthetic.main.search_recycler_item.view.*
import kotlinx.coroutines.launch

class SearchRecycleAdapter(private val weatherList: List<Location> ) :
    RecyclerView.Adapter<SearchRecycleAdapter.RecycleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.search_recycler_item,
            parent, false)
        return RecycleViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {

        val currentItem = weatherList[position]
        val image = currentItem.consolidated_weather[0].weather_state_abbr

        holder.imageView.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")

        holder.textView1.text = currentItem.title
        holder.textView2.text = currentItem.consolidated_weather[0].the_temp.toString()


    }


    override fun getItemCount() = weatherList.size

    inner class RecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.image_view
        val textView1: TextView = itemView.top_info
        val textView2: TextView = itemView.bottom_info

    }




}