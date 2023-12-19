package com.sundayweather.android.activity

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sundayweather.android.MyApplication
import com.sundayweather.android.R
import com.sundayweather.android.databinding.ActivityWeatherBinding
import com.sundayweather.android.logic.model.HourWeather
import com.sundayweather.android.logic.model.Weather
import com.sundayweather.android.logic.model.getSky
import com.sundayweather.android.ui.weather.HourlyAdapter
import com.sundayweather.android.ui.weather.WeatherViewModel
import com.sundayweather.android.utils.ToastUtil
import com.sundayweather.android.utils.showToast
import com.tencent.bugly.crashreport.CrashReport
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    lateinit var binding: ActivityWeatherBinding
    lateinit var adapter: HourlyAdapter
    private val hourlyWeatherList = ArrayList<HourWeather>()


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //沉浸式状态
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.locationLng.isEmpty()) viewModel.locationLng =
            intent.getStringExtra("location_lng") ?: ""
        if (viewModel.locationLat.isEmpty()) viewModel.locationLat =
            intent.getStringExtra("location_lat") ?: ""
        if (viewModel.placeName.isEmpty()) viewModel.placeName =
            intent.getStringExtra("place_name") ?: ""

        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                viewModel.saveWeather(weather)
                showWeatherInfo(weather)

            } else {
                if (viewModel.placeName.isNotEmpty()
                    && viewModel.locationLng.isNotEmpty()
                    && viewModel.locationLat.isNotEmpty()
                    && viewModel.isWeatherSaved()
                ) {
                    val saveWeather = viewModel.getSaveWeather()
                    showWeatherInfo(saveWeather)
                }

                "无法成功获取天气信息".showToast(MyApplication.context)
                result.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        })

        //该方法取消了下滑刷新功能,不在需求该功能,但recycleView 的划动不会受影响
        binding.includeHour.hourRecycleView.viewTreeObserver.addOnScrollChangedListener {
            if (binding.swipeRefreshLayout != null) {
                binding.swipeRefreshLayout.isEnabled = false

            }
        }

        binding.scrollViewLayout.viewTreeObserver.addOnScrollChangedListener {
            if (binding.swipeRefreshLayout != null) {
                binding.swipeRefreshLayout.isEnabled = binding.scrollViewLayout.scrollY == 0

            }
        }

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.gray_p30)
        refreshWeather()
        binding.swipeRefreshLayout.setOnRefreshListener { refreshWeather() }

        binding.switchover.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }

        binding.drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    decorView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }

        })

        binding.refresh.setOnClickListener { refreshWeather() }

        //设置24小时预报的RecyclerView
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.includeHour.hourRecycleView.layoutManager = layoutManager
        adapter = HourlyAdapter(hourlyWeatherList)
        binding.includeHour.hourRecycleView.adapter = adapter

    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        binding.swipeRefreshLayout.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        binding.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        val hourly = weather.hourly

        //填充now.xml的数据
        binding.includeNow.currentTemp.text = "${realtime.temperature.toInt()} ℃"
        binding.includeNow.currentSky.text = getSky(realtime.skycon).info
        binding.includeNow.currentAQI.text = "空气指数${realtime.air_quality.aqi.chn.toInt()}"
        //binding.includeNow.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        //hour.xml的数据
        hourlyWeatherList.clear()
        val hours = hourly.skycon.size
        for (i in 0 until hours) {
            val temVal = hourly.temperature[i].value
            val skyVal = hourly.skycon[i].value
            val datetime = hourly.skycon[i].datetime
            hourlyWeatherList.add(HourWeather(datetime, skyVal, temVal))
        }
        binding.includeHour.hourRecycleView.adapter?.notifyDataSetChanged()

        //填充forcast.xml的数据
        binding.includeForecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view =
                LayoutInflater.from(this)
                    .inflate(R.layout.forecast_item, binding.includeForecast.forecastLayout, false)
            val dataInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            dataInfo.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText
            binding.includeForecast.forecastLayout.addView(view)
        }

        //填充life_index.xml的数据
        val lifeIndex = daily.lifeIndex
        binding.includeLifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.includeLifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        binding.includeLifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.includeLifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc
        binding.weatherLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        binding.weatherLayout.visibility = View.VISIBLE
    }


}