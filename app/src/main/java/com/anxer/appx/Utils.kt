package com.anxer.appx

import android.app.ActivityManager
import android.content.Context

@Suppress("Deprecation")
object Utils {

    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = manager.getRunningServices(Int.MAX_VALUE)
        for (service in services) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}