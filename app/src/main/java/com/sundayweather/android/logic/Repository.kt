package com.sundayweather.android.logic

import androidx.lifecycle.liveData
import com.sundayweather.android.logic.dao.PlaceDao
import com.sundayweather.android.logic.dao.WeatherDao
import com.sundayweather.android.logic.model.Place
import com.sundayweather.android.logic.model.Weather
import com.sundayweather.android.logic.nerwork.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {


    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavePlace() = PlaceDao.getSavePlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun saveWeather(weather: Weather) = WeatherDao.saveWeather(weather)

    fun getSaveWeather() = WeatherDao.getSaveWeather()

    fun isWeatherSaved() = WeatherDao.isWeatherSaved()


    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferrenRealtime = async { SunnyWeatherNetwork.getRealtimeWeather(lng, lat) }
            val deferrenDaily = async { SunnyWeatherNetwork.getDailyWeather(lng, lat) }
            val deferrenHour = async { SunnyWeatherNetwork.getHourWeather(lng, lat) }
            val realtimeResponse = deferrenRealtime.await()
            val dailyResponse = deferrenDaily.await()
            val hourResponse = deferrenHour.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok" && hourResponse.status == "ok") {
                val weather = Weather(
                    realtimeResponse.result.realtime,
                    dailyResponse.result.daily,
                    hourResponse.result.hourly
                )
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtimeResponse response status is ${realtimeResponse.status}" +
                                "dailyResponse response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }


    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
}