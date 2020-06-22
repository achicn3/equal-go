package com.local.local.screen

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.local.local.R
import com.local.local.manager.LoginManager
import com.local.local.screen.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private fun toLocalPhone(phoneNumber: String?): String
        = "0${phoneNumber?.substring(4)}"

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
                R.id.nav_home, R.id.nav_friends, R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.findViewById<Button>(R.id.btn_drawer_logout).setOnClickListener {
            LoginManager.instance.logout()
            startActivity(Intent(this,LoginActivity::class.java))
        }
        val drawerAccountView : View by lazy { navView.getHeaderView(0)}
        val ivDrawerAvatar = drawerAccountView.findViewById<ImageView>(R.id.iv_drawer_avatar)
        val tvDrawerName = drawerAccountView.findViewById<TextView>(R.id.tv_drawer_name)
        val tvDrawerPhone = drawerAccountView.findViewById<TextView>(R.id.tv_drawer_cellphone)
        val loginListener = object : LoginManager.LoginListener{
            override fun onLogStateChange() {

            }

            override fun onUserInfoChange() {
                val cp = CircularProgressDrawable(this@MainActivity)
                cp.strokeWidth = 5f
                cp.centerRadius = 30f
                cp.setColorSchemeColors(R.color.colorGreen)
                cp.start()
                Glide
                    .with(this@MainActivity)
                    .load(LoginManager.instance.userData?.avatarUrl)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(cp)
                    .into(ivDrawerAvatar)
                tvDrawerName.text = LoginManager.instance.userData?.name
                tvDrawerPhone.text = toLocalPhone(LoginManager.instance.userData?.phone)
            }
        }
        LoginManager.instance.addListener(loginListener)
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
