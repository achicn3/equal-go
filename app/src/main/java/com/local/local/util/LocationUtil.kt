package com.local.local.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.local.local.extensions.Extensions.locationList
import com.local.local.manager.UserLoginManager
import java.text.SimpleDateFormat
import java.util.*

class LocationUtil {
    companion object {
        private const val seconds: Long = 1000L
        private const val minutes = 60 * seconds
        private val distanceMap = HashMap<String, Float>()
        private var client: FusedLocationProviderClient? = null
        private var lastLocation: Location? = null


        //每15分鐘更新距離會觸發的callback
        private val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                p0?.lastLocation?.run {
                    onLocationChanged(this)
                }
            }
        }

        //處理距離的邏輯
        private fun onLocationChanged(newLocation: Location){
            lastLocation = if(lastLocation == null){
                newLocation
            }else{
                //計算移動距離
                lastLocation?.apply {
                    val date = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(time)
                    val publicLocationDistance = FloatArray(1)
                    for (publicLocation in locationList) {
                        Location.distanceBetween(
                            latitude,
                            longitude,
                            publicLocation.latitude,
                            publicLocation.Longitude,
                            publicLocationDistance
                        )
                        if (publicLocationDistance[0] <= 25) {
                            FirebaseUtil.updateRecord(date, publicLocationDistance[0], 2)
                        }
                    }
                    val floatArray = FloatArray(1)
                    Location.distanceBetween(
                        latitude,
                        longitude,
                        newLocation.latitude,
                        newLocation.longitude,
                        floatArray
                    )
                    val time = Calendar.getInstance(Locale.TAIWAN).time
                    val moveDistance = distanceMap[date]?.plus(floatArray[0]) ?: floatArray[0]
                    distanceMap[date] = moveDistance
                    if (floatArray[0] >= 850f) {
                        FirebaseUtil.updateRecord(date, floatArray[0], 1)
                    } else if (floatArray[0] >= 25f) {//誤差為25公尺
                        FirebaseUtil.updateRecord(date, floatArray[0], 0)
                    }
                }
                UserLoginManager.instance.userData?.updateLocation(newLocation)
                FirebaseUtil.updateUserInfo()
                newLocation
            }
        }

        fun startLocationUpdates(context: Context,activity: Activity){
            val locationRequest = LocationRequest().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 15 * minutes //距離間隔

            }
            val locationSettingRequest = LocationSettingsRequest.Builder().apply {
                addLocationRequest(locationRequest)
            }.build()

            LocationServices.getSettingsClient(context).apply {
                checkLocationSettings(locationSettingRequest)
            }
            client = LocationServices.getFusedLocationProviderClient(context)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                PermissionUtil.requestLocation(activity,4991)
                return
            }
            client?.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        }
        //若使用者關閉程式，需要移除listener
        fun removeListener(){
            client?.removeLocationUpdates(locationCallback)
        }
    }
}