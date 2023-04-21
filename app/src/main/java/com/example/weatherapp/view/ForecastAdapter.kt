package com.example.weatherapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.LayoutForecastBinding
import com.example.weatherapp.model.DailyForecast

class ForecastAdapter(val forecasts: List<DailyForecast>): RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        context = parent.context
        val binding = LayoutForecastBinding.inflate(LayoutInflater.from(context), parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        with(holder) {
            with(forecasts[position]) {
                binding.tvDate.text = Date.split("T").get(0)
                binding.tvMaximum.text = "Maximum: ${Temperature.Maximum.Value} º${Temperature.Maximum.Unit}"
                binding.tvMinimum.text = "Minimum: ${Temperature.Minimum.Value} º${Temperature.Minimum.Unit}"
                binding.tvDayText.text = Day.IconPhrase
                val resourceSrc = "image" + Day.Icon
                binding.dayImage.setImageResource(context.getResources().getIdentifier(resourceSrc, "drawable", context.packageName))
                if(Day.HasPrecipitation) {
                    binding.tvDayPrecipitation.text = Day.PrecipitationType ?: "No precipitation expected"
                } else {
                    binding.tvDayPrecipitation.text = "No precipitation expected"
                }
                binding.tvNightText.text = Night.IconPhrase
                val resourceSrcNight = "image" + Night.Icon
                binding.nightImage.setImageResource(context.getResources().getIdentifier(resourceSrcNight, "drawable", context.packageName))
                if(Night.HasPrecipitation) {
                    binding.tvNightPrecipitation.text = Night.PrecipitationType ?: "No precipitation expected"
                } else {
                    binding.tvNightPrecipitation.text = "No precipitation expected"
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return forecasts.count()
    }

    inner class ForecastViewHolder(val binding: LayoutForecastBinding): RecyclerView.ViewHolder(binding.root)
}