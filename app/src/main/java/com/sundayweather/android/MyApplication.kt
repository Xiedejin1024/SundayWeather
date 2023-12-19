package com.sundayweather.android

import android.app.Application
import android.content.Context
import com.tencent.bugly.crashreport.CrashReport

class MyApplication : Application() {
    companion object {
        const val TOKEN = "O4D3anWdzoKDQO0Q"

        @SuppressWarnings("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        CrashReport.initCrashReport(context)
    }
}