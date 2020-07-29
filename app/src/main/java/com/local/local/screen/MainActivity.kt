package com.local.local.screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.local.local.R
import com.local.local.extensions.Extensions.loadCircleImage
import com.local.local.manager.UserLoginManager
import com.local.local.screen.login.LoginActivity
import com.local.local.util.LocationUtil


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private fun toLocalPhone(phoneNumber: String?): String
        = "0${phoneNumber?.substring(4)}"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        startService(Intent(this,OnClearFromRecentService::class.java))
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Intent(Intent.ACTION_SEND).run {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("boliagogo@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "[問題回報] ${UserLoginManager.instance.userData?.phone}")
                putExtra(Intent.EXTRA_TEXT, "請輸入您的問題：\n")
            }.run {
                try {
                    startActivity(Intent.createChooser(this, "回報問題"))
                }catch (e: ActivityNotFoundException){
                    Toast.makeText(
                        this@MainActivity,
                        "請直接聯絡boliagogo@gmail.com並說明問題。",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_friends, R.id.nav_map,R.id.nav_profile
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.findViewById<Button>(R.id.btn_drawer_logout).setOnClickListener {
            UserLoginManager.instance.logout()
            LocationUtil.removeListener()
            startActivity(Intent(this,LoginActivity::class.java))
        }

        val drawerAccountView : View by lazy { navView.getHeaderView(0)}
        drawerAccountView.setOnClickListener {
            if(navController.currentDestination?.id != R.id.nav_profile && navController.currentDestination?.id == R.id.nav_home) {
                navController.navigate(R.id.action_nav_home_to_profileInfoFragment)
                drawerLayout.closeDrawers()
            }
        }

        val ivDrawerAvatar = drawerAccountView.findViewById<ImageView>(R.id.iv_drawer_avatar)
        val tvDrawerName = drawerAccountView.findViewById<TextView>(R.id.tv_drawer_name)
        val tvDrawerPhone = drawerAccountView.findViewById<TextView>(R.id.tv_drawer_cellphone)
        val loginListener = object : UserLoginManager.LoginListener{
            override fun onLogStateChange() {

            }

            override fun onUserInfoChange() {
                ivDrawerAvatar.loadCircleImage(this@MainActivity,UserLoginManager.instance.userData?.avatarUrl)
                tvDrawerName.text = UserLoginManager.instance.userData?.name
                tvDrawerPhone.text = toLocalPhone(UserLoginManager.instance.userData?.phone)
            }
        }

        UserLoginManager.instance.addListener(loginListener)
        LocationUtil.startLocationUpdates(this,this)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
