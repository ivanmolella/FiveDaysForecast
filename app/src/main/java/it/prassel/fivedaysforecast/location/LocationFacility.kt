package it.prassel.kotlin.androidfacilitylib.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

import it.prassel.kotlin.androidfacilitylib.media.BadPermissionException
import it.prassel.kotlin.androidfacilitylib.media.IllegalStatusException
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.*


class LocationFacility(context: Context) {

    private val TAG = "LocationFacility"
    private var mCtx: Context? = null

    internal var mFusedLocationClient: FusedLocationProviderClient? = null
    internal var mLocationCallBack: LocationCallback? = null
    internal var mHandler = Handler(Looper.getMainLooper())

    interface LocationEventListener {

        fun onLocationFound(location: Location?)
        fun onUpdateLocation(location: List<Location>?)
        fun onLocationNotFound(e: Exception?)
        fun onBadSettingsForLocation()

    }

    init {
        mCtx = context
    }

    private fun initSingleton() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mCtx!!)
    }

    @SuppressLint("MissingPermission")
    @Throws(BadPermissionException::class, IllegalStatusException::class)
    fun startUpdateLocation(curActivity: Activity?, updateInterval: Long, stopAfter: Long, listener: LocationEventListener?) {

        Log.i(TAG, "-- <LocationFacility> startUpdateLocation")
        if (mFusedLocationClient == null) {
            throw IllegalStatusException("mFusedLocationClient not initialized")
        }

        Log.i(TAG, "-- <LocationFacility> startUpdateLocation checkLocationPermissions")
        checkLocationPermissions()
        Log.i(TAG, "-- <LocationFacility> startUpdateLocation checkLocationPermissions OK")

        Log.i(TAG, "-- <LocationFacility> startUpdateLocation checkLocationSettings")
        checkLocationSettings(curActivity, listener)
        Log.i(TAG, "-- <LocationFacility> startUpdateLocation checkLocationSettings OK")

        Log.i(TAG, "-- <LocationFacility> startUpdateLocation with interval: $updateInterval")

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = updateInterval // two minute interval
        mLocationRequest.fastestInterval = updateInterval
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mLocationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.i(TAG, "-- <LocationFacility> startUpdateLocation onLocationResult")
                if (listener != null && locationResult != null) {
                    listener.onUpdateLocation(locationResult.locations)
                } else {
                    if (locationResult == null) {
                        Log.i(TAG, "-- <LocationFacility> startUpdateLocation locationResult not found")
                    }
                }
            }
        }

        mFusedLocationClient!!.requestLocationUpdates(mLocationRequest, mLocationCallBack!!, Looper.getMainLooper())

        if (stopAfter > -1) {
            //Prova per stopAfter secondi
            mHandler.postDelayed({
                try {
                    stopUpdateLocation()
                } catch (e: IllegalStatusException) {
                    e.printStackTrace()
                }
            }, stopAfter)
        }
    }

    @Throws(IllegalStatusException::class)
    fun stopUpdateLocation() {
        Log.i(TAG, "-- <LocationFacility> stopUpdateLocation")

        if (mFusedLocationClient == null) {
            throw IllegalStatusException("mFusedLocationClient not initialized")
        }
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallBack!!)
    }

    @SuppressLint("MissingPermission")
    @Throws(BadPermissionException::class, IllegalStatusException::class)
    fun resolveAddressFromCoordinate(curActivity: Activity?, latitude : Double, longitude : Double) : Address? {

        var addresses: List<Address> = emptyList()

        val geocoder = Geocoder(curActivity, Locale.getDefault())

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    // In this sample, we get just a single address.
                    1)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        } catch (illegalArgumentException: IllegalArgumentException) {
            illegalArgumentException.printStackTrace()
        }

        if (addresses.isNotEmpty()){
            return addresses[0]
        }else{
            return null
        }
    }

    @SuppressLint("MissingPermission")
    @Throws(BadPermissionException::class, IllegalStatusException::class)
    fun getCurrentLocation(curActivity: Activity?, listener: LocationEventListener?) {

        if (mFusedLocationClient == null) {
            throw IllegalStatusException("mFusedLocationClient not initialized")
        }

        Log.i(TAG, "-- <LocationFacility> getCurrentLocation checkLocationPermissions")
        checkLocationPermissions()
        Log.i(TAG, "-- <LocationFacility> getCurrentLocation checkLocationPermissions OK")

        Log.i(TAG, "-- <LocationFacility> getCurrentLocation checkLocationSettings")
        checkLocationSettings(curActivity, listener)
        Log.i(TAG, "-- <LocationFacility> getCurrentLocation checkLocationSettings OK")


        mFusedLocationClient!!.lastLocation
                .addOnFailureListener { e ->
                    listener?.onLocationNotFound(e)
                }
                .addOnSuccessListener { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.i(TAG, "-- <LocationFacility> Location location")
                        if (listener != null) {
                            listener.onLocationFound(location)
                        } else {
                            Log.i(TAG, "-- <LocationFacility> onSuccess listener not found")
                        }
                    } else {
                        Log.i(TAG, "-- <LocationFacility> onSuccess Location null")
                        try {
                            findLocationWithUpdate(curActivity, listener)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            if (listener != null) {
                                listener.onLocationNotFound(null)
                            } else {
                                Log.i(TAG, "-- <LocationFacility> onSuccess listener not found")
                            }
                        }

                    }
                }

    }

    @Throws(BadPermissionException::class, IllegalStatusException::class)
    private fun findLocationWithUpdate(curActivity: Activity?, listener: LocationEventListener?) {
        Log.i(TAG, "-- <LocationFacility> findLocationWithUpdate")

        startUpdateLocation(curActivity, 100, 10000, object : LocationEventListener {

            internal var isUpdateNotified = false

            override fun onLocationFound(location: Location?) {}

            override fun onUpdateLocation(location: List<Location>?) {
                try {
                    mHandler.removeCallbacksAndMessages(null)
                    stopUpdateLocation()
                } catch (e: IllegalStatusException) {
                    e.printStackTrace()
                }

                if (isUpdateNotified == false) {
                    if (location != null && location.size > 0) {
                        isUpdateNotified = true
                        listener?.onLocationFound(location[0])
                    } else {
                        listener?.onLocationNotFound(null)
                    }
                }
            }

            override fun onLocationNotFound(e: Exception?) {
                listener?.onLocationNotFound(null)
            }

            override fun onBadSettingsForLocation() {
                listener?.onBadSettingsForLocation()
            }
        })
    }

    private fun checkLocationSettings(curActivity: Activity?, listener: LocationEventListener?) {

        val locReq = LocationRequest()
        locReq.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locReq)

        val task = LocationServices.getSettingsClient(mCtx!!).checkLocationSettings(builder.build())

        task.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            Log.i(TAG, "-- <LocationFacility> Settings not allow location tracking: $resolvable")

                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            if (curActivity != null) {
                                resolvable.startResolutionForResult(
                                        mCtx as Activity?,
                                        REQUEST_CHECK_SETTINGS)
                            } else {
                                listener?.onBadSettingsForLocation()
                            }

                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Log.i(TAG, "-- <LocationFacility> Settings change unavailable")
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        listener?.onBadSettingsForLocation()
                    }
                }
            }
        }

    }

    @Throws(BadPermissionException::class)
    private fun checkLocationPermissions() {

        if (ContextCompat.checkSelfPermission(mCtx!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(mCtx!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            throw BadPermissionException("Missing Permission", arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    fun requestRequiredPermissions(activity: Activity, e: BadPermissionException) {
        ActivityCompat.requestPermissions(activity, e.missingPermissions!!, PERMISSION_REQUEST)
    }

    companion object {

        var PERMISSION_REQUEST = 1000
        var REQUEST_CHECK_SETTINGS = 1050

        private var __instance: LocationFacility? = null

        fun instance(context: Context): LocationFacility {

            synchronized(LocationFacility::class.java) {
                if (__instance == null) {
                    __instance = LocationFacility(context)
                    __instance!!.initSingleton()
                }

                return __instance!!
            }
        }
    }

}
