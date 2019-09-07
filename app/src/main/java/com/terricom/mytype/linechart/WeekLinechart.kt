package com.terricom.mytype.linechart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.App
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.databinding.LinechartWeekBinding
import java.util.*
import kotlin.collections.ArrayList



class WeekLinechart : Fragment() {

    val currentDateTime = Calendar.getInstance()
    val thisWeek = mutableListOf<String>()
    val weeek = arrayListOf<String>()
    private val viewModel: WeeekLinechartViewModel by lazy {
        ViewModelProviders.of(this).get(WeeekLinechartViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = LinechartWeekBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val list = ArrayList<ChartEntity>()


        viewModel.carbonList.observe(this, androidx.lifecycle.Observer {
            if (it != null){
                Logger.i("WeekLinechart viewModel.fireDate.observe = ${it}")
                viewModel.waterList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        Logger.i("viewModel.waterList.observe(this, androidx.lifecycle.Observer it =$it")
                        val waterChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorWater), it)
                        list.add(waterChartEntity)

                    }
                })

                viewModel.oilList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val oilChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorOil), it)
                        list.add(oilChartEntity)
                    }
                })

                viewModel.vegetableList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val vegetableChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorVegetable), it)
                        list.add(vegetableChartEntity)

                    }
                })

                viewModel.proteinList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val proteinChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorProtein), it)
                        list.add(proteinChartEntity)
                    }
                })

                viewModel.fruitList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val fruitChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorFruit), it)
                        list.add(fruitChartEntity)

                    }
                })

                viewModel.carbonList.observe(this, androidx.lifecycle.Observer {
                    if (it != null){
                        val carbonChartEntity = ChartEntity(App.applicationContext().getColor(R.color.colorCarbon), it)
                        list.add(carbonChartEntity)
                    }
                })

                binding.lineChart.legendArray = viewModel.fireDate.value
                if (list != null){
                    binding.lineChart.setList(list)
                }
            }
        })



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