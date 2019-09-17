package com.terricom.mytype.shaperecord

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.*
import com.terricom.mytype.databinding.FragmentShapeRecordBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class ShapeRecordFragment: Fragment(), ShapeCalendarFragment.EventBetweenCalendarAndFragment {

    private val viewModel: ShapeRecordViewModel by lazy {
        ViewModelProviders.of(this).get(ShapeRecordViewModel::class.java)
    }
    private lateinit var binding: FragmentShapeRecordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShapeRecordBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToAchivementFragment())
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_achievment
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                (activity as MainActivity).fab.visibility = View.VISIBLE
            }
        }

        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

//        viewModel.date.value = "$year-$month-$day 12:00:00.000000000"



        binding.smartCustomCalendar.setEventHandler(this)
        binding.smartCustomCalendar.updateCalendar()

        viewModel.date.observe(this, androidx.lifecycle.Observer {
            Logger.i("ShapeRecordFragment viewModel.date.observe = $it")
        })

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.buttonBack2Main.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
            (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
            (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
            (activity as MainActivity).fab.visibility = View.VISIBLE
        }

        binding.buttonShaperecordSave.setOnClickListener {
            if (isConnected()) {
                Logger.i("NetworkConnection Network Connected.")
                //執行下載任務
            }else{
                Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT)
                //告訴使用者網路無法使用
            }
            viewModel.upDate("${binding.smartCustomCalendar.selectDateOut}")
            viewModel.addShape()
            viewModel.clearData()
        }

        return binding.root
    }

    private fun isConnected(): Boolean{
        val connectivityManager = App.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }


    override fun onCalendarNextPressed() {
        binding.smartCustomCalendar.updateCalendar()
    }

    override fun onCalendarPreviousPressed() {
        binding.smartCustomCalendar.updateCalendar()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout3.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout4.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false

    }

}