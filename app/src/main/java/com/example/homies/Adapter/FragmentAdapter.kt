package com.example.homies.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.homies.MainFragments.CallFragment
import com.example.homies.MainFragments.ChatFragment
import com.example.homies.MainFragments.StoryFragment

class FragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ChatFragment()
            }
            1 -> {
                StoryFragment()
            }
            2 -> {
                CallFragment()
            }
            else -> ChatFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "CHATS"
            1 -> "STORY"
            2 -> "CALLS"
            else -> "CHATS"
        }
    }
}