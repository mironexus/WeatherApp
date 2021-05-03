package com.example.weatherapp.adapters

import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginStart
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.weatherapp.CityActivity
import com.example.weatherapp.R
import com.example.weatherapp.SharedViewModel
import com.example.weatherapp.model.LocationCard
import kotlinx.android.synthetic.main.search_recycler_item.view.*

class SearchRecycleAdapter(private var locationCardList: MutableLiveData<List<LocationCard>>,
                           private val listener: OnItemClickListener,
                           private val sharedViewModel: SharedViewModel,
                           private val isMyCitiesList: Boolean
)
    : RecyclerView.Adapter<SearchRecycleAdapter.RecycleViewHolder>() {

    private var isEdit = false

    fun setEditable(edit: Boolean) {
        isEdit = edit
    }


    fun updateData(locationCardList: MutableLiveData<List<LocationCard>>) {
        this.locationCardList = locationCardList

        //find better notify method
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

        val currentItem = locationCardList.value?.get(position)

        if(currentItem != null) {
            holder.title.text = currentItem.title

            if(isMyCitiesList) {
                holder.coordinates.text = currentItem.time
                holder.distance.text = currentItem.timezone
            }
            else {
                holder.coordinates.text = currentItem.lattitude + " " + currentItem.longitude
                holder.distance.text = "Distance: " + currentItem.distance.toString() + " km"
            }

            holder.temperature.text = currentItem.the_temp.toInt().toString() + "°"

            val image = currentItem.weather_state_abbr
            holder.weatherStateAbbr.load("https://www.metaweather.com/static/img/weather/png/64/$image.png")

            if (currentItem.isMyCity) {
                holder.setMyCityButton.setImageResource(R.drawable.ic_star_1)
            }

            holder.setMyCityButton.setOnClickListener {

                if (currentItem.isMyCity) {
                    currentItem.isMyCity = false
                    holder.setMyCityButton.setImageResource(R.drawable.ic_star_0)
                    sharedViewModel.removeFromMyCities(currentItem.woeid)
                }
                else {
                    currentItem.isMyCity = true
                    holder.setMyCityButton.setImageResource(R.drawable.ic_star_1)
                    sharedViewModel.setAsMyCity(currentItem.woeid)
                }

            }

            if(isEdit) {
                holder.reorderIcon.visibility = View.VISIBLE
                holder.container.layoutParams.width = (Resources.getSystem().displayMetrics.widthPixels * 0.837).toInt()
                holder.innerContainer.layoutParams.width = (holder.container.layoutParams.width * 0.93).toInt()
            }
        }

    }

    override fun getItemCount() = locationCardList.value!!.size

    inner class RecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title: TextView = itemView.title
        val setMyCityButton: ImageView = itemView.set_my_city
        val coordinates: TextView = itemView.coordinates
        val distance: TextView = itemView.distance
        val temperature: TextView = itemView.temperature
        val weatherStateAbbr: ImageView = itemView.weather_state_abbr

        var container: CardView = itemView.location_card
        var reorderIcon: ImageView = itemView.reorder
        var innerContainer: ConstraintLayout = itemView.inner_container

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val currentItem = locationCardList.value?.get(position)
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
            if (v != null) {
                var intent = Intent(v.context, CityActivity::class.java)
                if (currentItem != null) {
                    intent.putExtra("woeid", currentItem.woeid)
                    intent.putExtra("isMyCity", currentItem.isMyCity)
                }
                v.context.startActivity(intent)
            }


        }
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


}