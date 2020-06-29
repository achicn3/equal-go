package com.local.local.screen.fragment.ui.points.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(private val fragmentList: ArrayList<Fragment>,
                       manager: FragmentManager,
                       lifecycle: Lifecycle) :
        FragmentStateAdapter(manager, lifecycle) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}