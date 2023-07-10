package com.sundayweather.android.logic.model

import com.sundayweather.android.logic.model.DailyResponse
import com.sundayweather.android.logic.model.RealtimeResponse

data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily, val hourly: HourResponse.Hourly)
