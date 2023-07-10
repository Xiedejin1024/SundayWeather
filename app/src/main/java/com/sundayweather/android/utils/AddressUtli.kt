package com.sundayweather.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import com.sundayweather.android.MyApplication


@SuppressLint("MissingPermission")
fun getLatAndLng(block: (address: String) -> Unit) {
    val locationManager = MyApplication.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
    if (location != null) {
        // 获取地址信息
        val address = getAddress(location.latitude, location.longitude)
        if (address == null || address == "") {
            "获取位置信息失败".showToast(MyApplication.context)
        } else {
            block(address)
        }
    } else {
        "获取位置信息失败，请检查是够开启GPS,是否授权".showToast(MyApplication.context)
    }


}

private fun getAddress(latitude: Double, longitude: Double): String {
    var cityName = ""
    try {
        val ge = Geocoder(MyApplication.context)
        var addressList = ge.getFromLocation(latitude, longitude, 1) as List<Address>
        if (addressList != null && addressList.isNotEmpty()) {
            for (address in addressList) {
                if (address.subLocality != null) {
                    cityName = if (address.thoroughfare != null) {
                        address.subLocality + address.thoroughfare
                    } else {
                        address.subLocality
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return cityName;
}