package com.terricom.mytype.shaperecord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.MainActivity
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.calendar.CalendarFragment
import com.terricom.mytype.databinding.FragmentShapeRecordBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.linechart_calendar.*
import java.util.*

class ShapeRecordFragment: Fragment(), CalendarFragment.EventBetweenCalendarAndFragment {

    private val viewModel: ShapeRecordViewModel by lazy {
        ViewModelProviders.of(this).get(ShapeRecordViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentShapeRecordBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToAchivementFragment())
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_line_chart
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                (activity as MainActivity).fab.visibility = View.VISIBLE
            }
        }

        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        binding.smartCustomCalendar.setEventHandler(this)
        binding.smartCustomCalendar.updateCalendar()

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        return binding.root
    }

    override fun onCalendarNextPressed() {
        smartCustomCalendar.updateCalendar()
    }

    override fun onCalendarPreviousPressed() {
        smartCustomCalendar.updateCalendar()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false

    }

}