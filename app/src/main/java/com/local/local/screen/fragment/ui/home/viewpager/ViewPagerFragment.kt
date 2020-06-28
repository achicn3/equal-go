package com.local.local.screen.fragment.ui.home.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.local.local.R
import com.local.local.screen.fragment.ui.home.daystatics.HomeFragment
import com.local.local.screen.fragment.ui.home.detailstatics.StaticsFragment

class ViewPagerFragment : Fragment() {
    private lateinit var vp: ViewPager2
    private lateinit var tabs: TabLayout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_viewpager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity ?: return super.onViewCreated(view, savedInstanceState)
        vp = view.findViewById(R.id.viewGroup_home_viewPager)
        tabs = view.findViewById(R.id.viewGroup_home_tabs)
        val fragmentList = arrayListOf(HomeFragment(), StaticsFragment())
        val vpAdapter = ViewPagerAdapter(fragmentList, activity.supportFragmentManager, lifecycle)
        vp.adapter = vpAdapter
        vp.setPageTransformer(ZoomOutPageTransformer())
        TabLayoutMediator(tabs, vp, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.home_today_statics)
                }
                1 -> {
                    tab.text = getString(R.string.home_details_statics)
                }
            }
        }).attach()
    }

}