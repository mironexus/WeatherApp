package com.example.weatherapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.weatherapp.databinding.WeatherInfoTileBinding

class WeatherInfoTile(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private var binding: WeatherInfoTileBinding

    init {
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = WeatherInfoTileBinding.inflate(layoutInflater)
        inflate(context, R.layout.weather_info_tile, this)
        addView(binding.root)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WeatherInfoTile,
            0, 0).apply {
            try {
                val title_text = getString(R.styleable.WeatherInfoTile_title)
                val value_text = getString(R.styleable.WeatherInfoTile_value)
                val icon_res = getResourceId(R.styleable.WeatherInfoTile_iconResource, 0)

                binding.weatherInfoTitle.text = title_text
                binding.weatherInfoValue.text = value_text
                binding.icon.setImageResource(icon_res)


            }
            finally {
                recycle()
            }
        }
    }

    fun setValue(value: String) {
        binding.weatherInfoValue.text = value
    }

}