package com.local.local.util

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.local.local.manager.UserLoginManager
import java.text.SimpleDateFormat
import java.util.*

class LocationUtil {
    companion object{
        private const val seconds : Long = 1000L
        private val distanceMap = HashMap<String,Float>()
        private var client : FusedLocationProviderClient? = null
        private var lastLocation : Location? = null


        //每15分鐘更新距離會觸發的callback
        private val locationCallback = object : LocationCallback(){
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
                    val floatArray = FloatArray(1)
                    Location.distanceBetween(latitude,longitude,newLocation.latitude,newLocation.longitude,floatArray)
                    val time = Calendar.getInstance(Locale.TAIWAN).time
                    val date = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(time)
                    val moveDistance = distanceMap[date]?.plus(floatArray[0]) ?: floatArray[0]
                    distanceMap[date] = moveDistance
                    if(floatArray[0]>850f){
                        FirebaseUtil.updateRecord(date,floatArray[0],1)
                    }else{
                        FirebaseUtil.updateRecord(date,floatArray[0],0)
                    }
                }
                UserLoginManager.instance.userData?.updateLocation(newLocation)
                FirebaseUtil.updateUserInfo()
                newLocation
            }
        }

        fun startLocationUpdates(context: Context){
            val locationRequest = LocationRequest().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 3*seconds
            }
            val locationSettingRequest = LocationSettingsRequest.Builder().apply {
                addLocationRequest(locationRequest)
            }.build()

            LocationServices.getSettingsClient(context).apply {
                checkLocationSettings(locationSettingRequest)
            }
            client = LocationServices.getFusedLocationProviderClient(context)
            if(!PermissionUtil.hasGrantedLocation(context))
                return
            client?.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        }

        fun removeListener(){
            client?.removeLocationUpdates(locationCallback)
        }
    }
}