package com.sundayweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sundayweather.android.MyApplication
import com.sundayweather.android.logic.model.Place

object PlaceDao {
    fun savePlace(place: Place) {
        sharedPreferences().edit { putString("place", Gson().toJson(place)) }
    }

    fun getSavePlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")
    private fun sharedPreferences() =
        MyApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}