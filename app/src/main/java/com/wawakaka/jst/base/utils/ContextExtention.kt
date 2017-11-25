package com.wawakaka.jst.base.utils

import android.app.NotificationManager
import android.content.Context
import android.graphics.Point
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * Created by wawakaka on 7/25/2017.
 */

fun Context.isNetworkAvailable(): Boolean {
    val activeNetworkInfo = getSystemService<ConnectivityManager>(Context.CONNECTIVITY_SERVICE)?.activeNetworkInfo
    return activeNetworkInfo?.isConnected ?: false
}

fun Context.clearAllShownNotifications() {
    val notificationManager = getSystemService<NotificationManager>(Context.NOTIFICATION_SERVICE)
    notificationManager?.cancelAll()
}

inline fun <reified T> Context.getSystemService(systemService: String): T? {
    return getSystemService(systemService) as T
}

const val WIDTH_INDEX = 0
const val HEIGHT_INDEX = 1

fun Context.getScreenSize(): IntArray {
    val widthHeight = IntArray(2)
    widthHeight[WIDTH_INDEX] = 0
    widthHeight[HEIGHT_INDEX] = 0

    val windowManager = this.getSystemService<WindowManager>(Context.WINDOW_SERVICE)
    val display = windowManager?.defaultDisplay

    val size = Point()
    display?.getSize(size)
    widthHeight[WIDTH_INDEX] = size.x
    widthHeight[HEIGHT_INDEX] = size.y

    if (!isScreenSizeRetrieved(widthHeight)) {
        val metrics = DisplayMetrics()
        display?.getMetrics(metrics)
        widthHeight[0] = metrics.widthPixels
        widthHeight[1] = metrics.heightPixels
    }

    // Last defense. Use deprecated API that was introduced in lower than API 13
    if (!isScreenSizeRetrieved(widthHeight)) {
        widthHeight[0] = display?.width ?: 0 // deprecated
        widthHeight[1] = display?.height ?: 0 // deprecated
    }

    return widthHeight
}

private fun isScreenSizeRetrieved(widthHeight: IntArray): Boolean {
    return widthHeight[WIDTH_INDEX] != 0 && widthHeight[HEIGHT_INDEX] != 0
}

const val BOTTOM_SHEET_RATIO = 9f / 16f

fun Context.getBottomSheetDefaultCollapsedHeight(): Float {
    return BOTTOM_SHEET_RATIO * getScreenSize()[HEIGHT_INDEX]
}
