package com.local.local.screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.local.local.R
import com.local.local.screen.login.LoginActivity
import kotlinx.android.synthetic.main.activity_welcom.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcom)
        first.playAnimation()
        GlobalScope.launch(Dispatchers.Main) {
            delay(1300)
            Intent(this@FirstActivity, LoginActivity::class.java).also {
                it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(it)
                finish()
            }
        }

    }
}