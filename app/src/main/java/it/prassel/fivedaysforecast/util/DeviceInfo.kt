package it.prassel.fivedaysforecast.util

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.net.wifi.WifiManager
import android.util.DisplayMetrics
import android.util.Log
import it.prassel.fivedaysforecast.R


class DeviceInfo {


    var width = 0
    var height = 0
    var density = 0.0

    override fun toString(): String {
        return ("DeviceInfo [width=" + width + ", height=" + height
                + ", density=" + density + "]")
    }

    init {

        val dm = Resources.getSystem().displayMetrics

        this.width = dm.widthPixels
        this.height = dm.heightPixels
        this.density = dm.density.toDouble()

        Log.d(TAG, "-- Device Info: w: " + this.width + " y: " + this.height + " d: " + this.density)
    }

    companion object {

        internal val TAG = "DeviceInfo"

        val DEVICE_TYPE_PHONE = "Phone"
        val DEVICE_TYPE_TABLET = "Tablet"

        fun getStatusBarHeight(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        fun getDeviceSlotSize(splitFactor: Int, context: Context): Int {

            val dInfo = DeviceInfo()
            val statusBarHeight = DeviceInfo.getStatusBarHeight(context)

            val devHeight = dInfo.height - statusBarHeight

            return devHeight / splitFactor
        }

        fun forceOrientation4Device(activity: Activity) {

            val isTablet = activity.resources.getBoolean(R.bool.isTablet)

            if (isTablet == true) {
                Log.v(TAG, "-- forceLandscapeIfTablet")
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                Log.v(TAG, "-- forcePortraitIfSmartPhone")
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

        }

        fun dpiToPx(dpi: Int): Int {

            val dInfo = DeviceInfo()
            return (dpi * dInfo.density).toInt()

        }

        fun getWifiId(ctx: Context): String {
            val wm = ctx.getSystemService(Context.WIFI_SERVICE) as WifiManager
            return ""//wm.connectionInfo.macAddress
        }
    }
}
