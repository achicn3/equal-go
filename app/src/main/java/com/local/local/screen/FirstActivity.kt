package com.local.local.screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.local.local.R
import com.local.local.screen.login.LoginActivity
import com.local.local.util.PermissionRationalActivity
import com.local.local.util.PermissionUtil
import kotlinx.android.synthetic.main.activity_welcom.*


class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcom)
        first.playAnimation()

        if (!PermissionUtil.hasGrantedReadWriteExternalStorage(this) or !PermissionUtil.hasGrantedActivity(this) or !PermissionUtil.hasGrantedLocation(this)
            or !PermissionUtil.hasGrantedRecordAudio(this)) {
            startActivity(Intent(this, PermissionRationalActivity::class.java))
            finish()
        }
        if (PermissionUtil.hasGrantedLocation(this) && PermissionUtil.hasGrantedCamera(this) && PermissionUtil.hasGrantedReadWriteExternalStorage(this)){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
}