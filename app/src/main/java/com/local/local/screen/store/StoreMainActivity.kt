package com.local.local.screen.store

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.local.local.R
import com.local.local.screen.admin.userlist.UserListFragment
import com.local.local.screen.admin.verification.VerificationFragment
import com.local.local.screen.fragment.ui.points.viewpager.ViewPagerAdapter
import com.local.local.screen.fragment.ui.points.viewpager.ZoomOutPageTransformer
import com.local.local.screen.login.LoginActivity
import com.local.local.screen.store.items.StoreAddItemFragment
import com.local.local.screen.store.record.StoreRecordFragment
import kotlinx.android.synthetic.main.activity_store_main.*

class StoreMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_main)
        val vp = findViewById<ViewPager2>(R.id.viewPager_storeMain)
        val tabs = findViewById<TabLayout>(R.id.tabs_storeMain)
        val fragmentList = arrayListOf<Fragment>(StoreRecordFragment(),StoreAddItemFragment())
        val vpAdapter = ViewPagerAdapter(fragmentList, supportFragmentManager, lifecycle)
        fab_logout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        vp.apply {
            adapter = vpAdapter
            setPageTransformer(ZoomOutPageTransformer())
        }

        TabLayoutMediator(
            tabs,
            vp,
            TabLayoutMediator.TabConfigurationStrategy { tabs, position ->
                when (position) {
                    0 -> {
                        tabs.text = "查詢\n兌換紀錄"
                    }
                    1 -> {
                        tabs.text = "新增/編輯\n優惠券"
                    }
                }
            }).attach()
    }
}