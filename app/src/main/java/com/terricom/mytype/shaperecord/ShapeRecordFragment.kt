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
import com.terricom.mytype.databinding.FragmentShapeRecordBinding
import kotlinx.android.synthetic.main.activity_main.*

class ShapeRecordFragment: Fragment() {

    private val viewModel: ShapeRecordViewModel by lazy {
        ViewModelProviders.of(this).get(ShapeRecordViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentShapeRecordBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.calendarView.setOnDateChangeListener { calendarView, i, j, k ->
            if (j<10){
                viewModel.setDate("$i.0$j.$k")
            }else viewModel.setDate("$i.$j.$k")
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToAchivementFragment())
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_line_chart
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
        (activity as MainActivity).fab.visibility = View.VISIBLE
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false




    }

}