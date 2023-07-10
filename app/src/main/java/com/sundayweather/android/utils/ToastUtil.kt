package com.sundayweather.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.sundayweather.android.MyApplication
import com.sundayweather.android.R


private var oldMsg: String? = null
private var oneTime: Long = 0
private var twoTime: Long = 0
private var toast: Toast? = null

fun String.showToast(context: Context?, duration: Int = Toast.LENGTH_LONG) {
    if (toast == null) {
        toast = Toast.makeText(context, this, duration)
        toast?.show()
        oneTime = System.currentTimeMillis()
    } else {
        twoTime = System.currentTimeMillis()
        if (this == oldMsg) {
            if (twoTime - oneTime > 2000) {
                // 这里是判断toast上一次显示的时间和这次的显示时间如果大于2000，
                //  则显示新的toast
                toast!!.cancel()
                toast = Toast.makeText(context, this, duration)
                toast?.show()
                oneTime = twoTime
            }
        } else {
            toast!!.cancel()
            toast = Toast.makeText(context, this, duration)
            toast?.show()
            oldMsg = this
            oneTime = twoTime
        }
    }
}


fun Int.showToast(duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(MyApplication.context, this, duration).show()
}


@SuppressLint("WrongConstant")
object ToastUtil {
    fun showTextCustomView(
        context: Context,
        text: String,
        duration: Int = Toast.LENGTH_LONG,
        block: (text: String) -> View
    ) {
        if (toast == null) {
            toast = Toast.makeText(context, text, duration)
            toast?.view = block(text)
            toast?.show()
            oneTime = System.currentTimeMillis()
        } else {
            twoTime = System.currentTimeMillis()
            if (text == oldMsg) {
                if (twoTime - oneTime > 2000) {
                    // 这里是判断toast上一次显示的时间和这次的显示时间如果大于2000，
                    //  则显示新的toast
                    toast!!.cancel()
                    toast = Toast.makeText(context, text, duration)
                    toast?.view = (block(text))
                    toast?.show()
                    oneTime = twoTime
                }
            } else {
                toast!!.cancel()
                toast = Toast.makeText(context, text, duration)
                toast?.view = (block(text))
                toast?.show()
                oldMsg = text
                oneTime = twoTime
            }
        }
    }

}

