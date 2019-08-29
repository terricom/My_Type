package com.terricom.mytype.achivement

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.terricom.mytype.App
import com.terricom.mytype.R

class AchivementAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return ListAchivement()
            1 -> return WeekAchivement()
            else -> return MonthAchivement()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        when(position){
            0 -> return App.applicationContext().getString(R.string.achivement_tab_list)
            1 -> return App.applicationContext().getString(R.string.achivement_tab_week)
            else -> return App.applicationContext().getString(R.string.achivement_tab_month)

        }
    }
}