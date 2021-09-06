package com.example.guruapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val items=ArrayList<Fragment>()

    init {
        items.add(main_first.newInstance(1))
        items.add(main_second.newInstance("id"))
    }
    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> main_first()
            else -> main_second()

        }
    }

    override fun getCount(): Int {
        return 2
    }
}