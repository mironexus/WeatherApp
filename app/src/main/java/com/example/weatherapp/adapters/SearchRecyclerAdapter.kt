package com.example.weatherapp.adapters

import android.app.Application
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.CityActivity
import com.example.weatherapp.R
import com.example.weatherapp.SharedViewModel
import com.example.weatherapp.model.LocationCard
import kotlinx.android.synthetic.main.search_recycler_item.view.*
import java.io.Serializable

class SearchRecycleAdapter(private var weatherList: MutableLiveData<List<LocationCard>>,
                           private val listener: OnItemClickListener,
                           private val sharedViewModel: SharedViewModel
)
    : RecyclerView.Adapter<SearchRecycleAdapter.RecycleViewHolder>() {


    fun updateData(weatherList: MutableLiveData<List<LocationCard>>) {
        this.weatherList = weatherList

        //finc better notify method
        notifyDataSetChanged()
    }

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
            val image = currentItem.weather_state_abbr
            holder.imageView.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")

            holder.textView1.text = currentItem.title
            holder.textView2.text = currentItem.the_temp.toString()


            if (currentItem.isMyCity) {
                holder.setMyCityButton.setImageResource(R.drawable.ic_star_1)
            }

        }

    }

    override fun getItemCount() = weatherList.value!!.size

    inner class RecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val imageView: ImageView = itemView.image_view
        val textView1: TextView = itemView.top_info
        val textView2: TextView = itemView.bottom_info
        val setMyCityButton: ImageView = itemView.set_my_city

        init {
            itemView.setOnClickListener(this)
            setMyCityButton.setOnClickListener {

                val position = adapterPosition
                val currentItem = weatherList.value?.get(position)

                if (currentItem != null) {
                    sharedViewModel.setAsMyCity(currentItem.woeid)
                    currentItem.isMyCity = true
                }
            }
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
                    intent.putExtra("woeid", currentItem.woeid)
                }
                v.context.startActivity(intent)
            }


        }
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


}