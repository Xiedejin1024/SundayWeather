package com.sundayweather.android.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sundayweather.android.logic.Repository
import com.sundayweather.android.logic.model.Location
import com.sundayweather.android.logic.model.Place
import com.sundayweather.android.logic.model.Weather

class WeatherViewModel : ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()
    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    val weatherLiveData = Transformations.switchMap(locationLiveData) { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
    }


    fun saveWeather(weather: Weather) = Repository.saveWeather(weather)

    fun getSaveWeather() = Repository.getSaveWeather()

    fun isWeatherSaved() = Repository.isWeatherSaved()


}