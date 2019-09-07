package com.terricom.mytype.linechart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.databinding.LinechartMonthBinding
import java.util.*
import kotlin.collections.ArrayList

class MonthLinechart : Fragment() {

    val currentDateTime = Calendar.getInstance()
    val thisWeek = mutableListOf<String>()
    val weeek = arrayListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = LinechartMonthBinding.inflate(inflater)
        binding.lifecycleOwner = this

        getThisWeek()

        val waterChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorWater), graph1)
        val oilChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorOil), graph2)

        val list = ArrayList<ChartEntity>()
        list.add(waterChartEntity)
        list.add(oilChartEntity)

        binding.lineChart.legendArray = thisWeek.toTypedArray()
        binding.lineChart.setList(list)
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


    private val graph1 = floatArrayOf(10f, 7f, 6f, 5f, 4f, 3f, 7f)
    private val graph2 = floatArrayOf(0f, 2f, 10f,8f, 7f, 6f, 4f)



}