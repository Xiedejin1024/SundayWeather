package com.sundayweather.android.logic.nerwork

import com.sundayweather.android.MyApplication
import com.sundayweather.android.logic.model.DailyResponse
import com.sundayweather.android.logic.model.HourResponse
import com.sundayweather.android.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherService {
    @GET("v2.5/${MyApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<RealtimeResponse>

    @GET("v2.5/${MyApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<DailyResponse>

    @GET("v2.5/${MyApplication.TOKEN}/{lng},{lat}/hourly?hourlysteps=24")
    fun getHourWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<HourResponse>

}