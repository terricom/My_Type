package com.terricom.mytype.linechart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import java.text.SimpleDateFormat
import java.util.*


class LinechartFragment: Fragment() {

    val currentDateTime = Calendar.getInstance()
    val thisWeek = mutableListOf<String>()
    val weeek = arrayListOf<String>()
    private val viewModel: LinechartViewModel by lazy {
        ViewModelProviders.of(this).get(LinechartViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentLinechartBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        var currentPosition = 0

        viewModel.listDates.observe(this, androidx.lifecycle.Observer {
            if (it != null && it.isNotEmpty() && it[0].values.isNotEmpty()){

                binding.lineChart.legendArray = viewModel.fireDate.value
                binding.lineChart.setList(it)
                binding.lineChart.visibility = View.VISIBLE
                binding.iconMyType.visibility = View.GONE
                binding.shaperecordHint.visibility = View.GONE
            } else if (it != null && it[0].values.isEmpty()){
                binding.lineChart.visibility = View.GONE
                binding.iconMyType.visibility = View.VISIBLE
                binding.shaperecordHint.visibility = View.VISIBLE
            }
        })


        viewModel.recordDate.observe(this, androidx.lifecycle.Observer {
            viewModel.getThisMonth()
        })

        binding.toBack.setOnClickListener {
            viewModel.newFireBack()
            currentPosition = currentPosition-1
            viewModel.setDate(Date(Date().time.plus(604800000L*(currentPosition))))
            viewModel.getThisMonth()
        }
        binding.toNext.setOnClickListener {
            viewModel.newFireBack()
            currentPosition = currentPosition+1
            viewModel.setDate(Date(Date().time.plus(604800000L*(currentPosition))))
            viewModel.getThisMonth()
//            binding.viewPager2.setCurrentItem( currentPosition.plus(1), true)
        }

        return binding.root
    }

    private fun getThisWeek(){
        val currentMonth = currentDateTime.get(Calendar.MONTH) + 1
        val lastDayOfLastMonth = getLastMonthLastDate()
        val currentDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        for (i in currentDay-6 until  currentDay ){
            if (i>-1){
                weeek.add("$currentMonth/$i")
                thisWeek.add("$currentMonth/$i")
            }else {
                weeek.add("${currentMonth-1}/${lastDayOfLastMonth+i}")
                thisWeek.add("${currentMonth-1}/${lastDayOfLastMonth+i}")
            }
        }
        thisWeek.add("${currentMonth}/${currentDay}")

    }

    fun getLastMonthLastDate(): Int {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)

        val max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, max)

        return calendar.get(Calendar.DAY_OF_MONTH)
    }




}