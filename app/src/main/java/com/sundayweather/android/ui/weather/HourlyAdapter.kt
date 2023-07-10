package com.sundayweather.android.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sundayweather.android.databinding.HourItemBinding
import com.sundayweather.android.databinding.PlaceItemBinding
import com.sundayweather.android.logic.model.HourWeather
import com.sundayweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class HourlyAdapter(private val hourlyList: List<HourWeather>) :
    RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    inner class ViewHolder(binding: HourItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val hourDate: TextView = binding.tvHourDate
        val hourIcon: ImageView = binding.imageHourIcon
        val hourTemp: TextView = binding.tvHourTemp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = HourItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = hourlyList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hourWeather = hourlyList[position]
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.hourDate.text = simpleDateFormat.format(hourWeather.date)
        holder.hourIcon.setImageResource(getSky(hourWeather.skyValue).icon)
        holder.hourTemp.text = "${hourWeather.tmpValue.toInt()}°"
    }


}