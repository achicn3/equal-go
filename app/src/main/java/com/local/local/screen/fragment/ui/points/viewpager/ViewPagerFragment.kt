package com.local.local.screen.fragment.ui.points.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.local.local.R
import com.local.local.screen.fragment.ui.points.daystatics.HomeFragment
import com.local.local.screen.fragment.ui.points.detailstatics.StaticsFragment
import com.local.local.screen.fragment.ui.points.transaction.exchange.TransactionFragment
import com.local.local.screen.fragment.ui.points.transaction.record.TransactionRecord

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
        val fragmentList = arrayListOf(HomeFragment(), StaticsFragment(),StaticsFragment("點數"), TransactionFragment(),TransactionRecord())
        val vpAdapter = ViewPagerAdapter(fragmentList, activity.supportFragmentManager, lifecycle)
        vp.adapter = vpAdapter
        vp.setPageTransformer(ZoomOutPageTransformer())
        TabLayoutMediator(tabs, vp, TabLayoutMediator.TabConfigurationStrategy { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.view_pager_title_home_today_statics)
                }
                1 -> {
                    tab.text = getString(R.string.viewpager_title_distance_details_statics)
                }
                2 -> {
                    tab.text = getString(R.string.viewpager_title_points_details_statics)
                }
                3->{
                    tab.text = getString(R.string.viewpager_title_exchange_item)
                }
                4 ->{
                    tab.text = getString(R.string.viewpager_title_exchange_record)
                }
            }
        }).attach()
    }

}