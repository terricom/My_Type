package com.terricom.mytype.linechart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.terricom.mytype.App
import com.terricom.mytype.R

class LinechartAdapter (fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return CalendarLinechart()
            1 -> return WeekLinechart()
            else -> return MonthLinechart()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        when(position){
            0 -> return App.applicationContext().getString(R.string.linechart_tab_title_calendar)
            1 -> return App.applicationContext().getString(R.string.linechart_tab_title_week)
            else -> return App.applicationContext().getString(R.string.linechart_tab_title_month)

        }
    }
}