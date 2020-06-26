package com.local.local.screen

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.local.local.R
import com.local.local.extensions.Extensions.loadCircleImage
import com.local.local.manager.LoginManager
import com.local.local.screen.login.LoginActivity
import com.local.local.util.FirebaseUtil
import com.local.local.util.PermissionUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    private val seconds : Long = 1000L
    private val distanceMap = HashMap<String,Float>()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private fun toLocalPhone(phoneNumber: String?): String
        = "0${phoneNumber?.substring(4)}"
    private var client : FusedLocationProviderClient? = null
    var lastLocation : Location? = null

    //每15分鐘更新距離會觸發的callback
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            p0?.lastLocation?.run {
               onLocationChanged(this)
            }
        }
    }

    //處理距離的邏輯
    fun onLocationChanged(newLocation: Location){
        lastLocation = if(lastLocation == null){
            newLocation
        }else{
            //計算移動距離
            lastLocation?.apply {
                val floatArray = FloatArray(1)
                Location.distanceBetween(latitude,longitude,newLocation.latitude,newLocation.longitude,floatArray)
                Log.d("status","the move distance is : ${floatArray[0]}")
                val time = Calendar.getInstance(Locale.TAIWAN).time
                val date = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(time)
                var moveDistance = distanceMap[date]?.plus(floatArray[0]) ?: floatArray[0]
                distanceMap[date] = moveDistance
                if(floatArray[0]>850f){
                    FirebaseUtil.updateRecord(date,floatArray[0],1)
                }else{
                    FirebaseUtil.updateRecord(date,floatArray[0],0)
                }
            }
            LoginManager.instance.userData?.updateLocation(newLocation)
            FirebaseUtil.updateUserInfo()
            newLocation
        }
    }

    private fun startLocationUpdates(){
        val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 3*seconds
        }
        val locationSettingRequest = LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
        }.build()

        LocationServices.getSettingsClient(this).apply {
            checkLocationSettings(locationSettingRequest)
        }
        client = LocationServices.getFusedLocationProviderClient(this)
        if(!PermissionUtil.hasGrantedLocation(this))
            return
        client?.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_friends, R.id.nav_map
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.findViewById<Button>(R.id.btn_drawer_logout).setOnClickListener {
            LoginManager.instance.logout()
            client?.removeLocationUpdates(locationCallback)
            startActivity(Intent(this,LoginActivity::class.java))
        }

        val drawerAccountView : View by lazy { navView.getHeaderView(0)}
        drawerAccountView.setOnClickListener {
            if(navController.currentDestination?.id != R.id.profileInfoFragment) {
                navController.navigate(R.id.action_nav_home_to_profileInfoFragment)
                drawerLayout.closeDrawers()
            }
        }

        val ivDrawerAvatar = drawerAccountView.findViewById<ImageView>(R.id.iv_drawer_avatar)
        val tvDrawerName = drawerAccountView.findViewById<TextView>(R.id.tv_drawer_name)
        val tvDrawerPhone = drawerAccountView.findViewById<TextView>(R.id.tv_drawer_cellphone)
        val loginListener = object : LoginManager.LoginListener{
            override fun onLogStateChange() {

            }

            override fun onUserInfoChange() {
                ivDrawerAvatar.loadCircleImage(this@MainActivity,LoginManager.instance.userData?.avatarUrl)
                tvDrawerName.text = LoginManager.instance.userData?.name
                tvDrawerPhone.text = toLocalPhone(LoginManager.instance.userData?.phone)
            }
        }

        LoginManager.instance.addListener(loginListener)
        startLocationUpdates()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
