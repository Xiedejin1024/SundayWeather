package com.sundayweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sundayweather.android.MyApplication
import com.sundayweather.android.logic.model.Place
import com.sundayweather.android.logic.model.Weather

object WeatherDao {
    fun saveWeather(weather: Weather) {
        sharedPreferences().edit { putString("weather", Gson().toJson(weather)) }
    }

    fun getSaveWeather(): Weather {
        val weatherJson = sharedPreferences().getString("weather", "")
        return Gson().fromJson(weatherJson, Weather::class.java)
    }

    fun isWeatherSaved() = sharedPreferences().contains("weather")
    private fun sharedPreferences() =
        MyApplication.context.getSharedPreferences("before_weather", Context.MODE_PRIVATE)
}