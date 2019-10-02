package com.terricom.mytype.linechart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.databinding.LinechartCalendarBinding
import java.util.*


class CalendarLinechart : Fragment()
//    , CalendarComponentLayout.EventBetweenCalendarAndFragment
{

    private val viewModel: CalendarLinechartViewModel by lazy {
        ViewModelProviders.of(this).get(CalendarLinechartViewModel::class.java)
    }

    private lateinit var binding: LinechartCalendarBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LinechartCalendarBinding.inflate(inflater)
        binding.setLifecycleOwner(this)

        binding.viewModel = viewModel

        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


//        binding.smartCustomCalendar.setEventHandler(this)
//        binding.smartCustomCalendar.updateCalendar()


        viewModel.date.observe(this, androidx.lifecycle.Observer {
            Log.i("Terri", "viewModel.date.observe =$it")
        })



        return binding.root
    }

//    override fun onCalendarNextPressed() {
//        binding.smartCustomCalendar.updateCalendar()
//    }
//
//    override fun onCalendarPreviousPressed() {
//        binding.smartCustomCalendar.updateCalendar()
//    }



}