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
import com.example.weatherapp.model.Location
import kotlinx.android.synthetic.main.search_recycler_item.view.*
import java.io.Serializable

class SearchRecycleAdapter(private var weatherList: MutableLiveData<List<Location>>,
                           private val listener: OnItemClickListener
)
    : RecyclerView.Adapter<SearchRecycleAdapter.RecycleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.search_recycler_item,
            parent, false)
        return RecycleViewHolder(
            itemView
        )
    }

    override fun onBindViewHolder(holder: RecycleViewHolder, position: Int) {

        val currentItem = weatherList.value?.get(position)

        if(currentItem != null) {
            val image = currentItem.consolidated_weather[0].weather_state_abbr

            holder.imageView.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")

            holder.textView1.text = currentItem.title
            holder.textView2.text = currentItem.consolidated_weather[0].the_temp.toString()
        }

    }

    override fun getItemCount() = weatherList.value!!.size

    inner class RecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val imageView: ImageView = itemView.image_view
        val textView1: TextView = itemView.top_info
        val textView2: TextView = itemView.bottom_info

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val currentItem = weatherList.value?.get(position)
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
            if (v != null) {
                var intent = Intent(v.context, CityActivity::class.java)
                if (currentItem != null) {
                    intent.putExtra("title", currentItem.title)
                    intent.putExtra("woeid", currentItem.woeid)
                    intent.putExtra("consolidated_weather", currentItem.consolidated_weather as Serializable)
                }
                v.context.startActivity(intent)
            }


        }
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }




}