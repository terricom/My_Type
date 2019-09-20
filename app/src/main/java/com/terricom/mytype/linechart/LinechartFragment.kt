package com.terricom.mytype.linechart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_linechart.*
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
                binding.let {
                    recycler_foodie_sum.visibility = View.VISIBLE
                    icon_water_goal.visibility = View.VISIBLE
                    icon_body_water.visibility = View.VISIBLE
                    icon_weight.visibility = View.VISIBLE
                    icon_body_age.visibility = View.VISIBLE
                    icon_body_fat.visibility = View.VISIBLE
                    icon_muscle.visibility = View.VISIBLE
                    icon_tdee.visibility = View.VISIBLE
                    icon_calendar.visibility = View.VISIBLE
                    number_body_age_show.visibility = View.VISIBLE
                    number_body_fat_show.visibility = View.VISIBLE
                    number_body_water_show.visibility = View.VISIBLE
                    number_muscle_show.visibility = View.VISIBLE
                    number_tdeet_show.visibility = View.VISIBLE
                    number_weight_show.visibility = View.VISIBLE
                    date_show.visibility = View.VISIBLE
                    diff_carbon.visibility = View.VISIBLE
                    diff_fruit.visibility = View.VISIBLE
                    diff_oil.visibility = View.VISIBLE
                    diff_protein.visibility = View.VISIBLE
                    diff_vegetable.visibility = View.VISIBLE
                    diff_water.visibility = View.VISIBLE
                    goal_carbon.visibility = View.VISIBLE
                    goal_fruit.visibility = View.VISIBLE
                    goal_vegetable.visibility = View.VISIBLE
                    goal_oil.visibility = View.VISIBLE
                    goal_protein.visibility = View.VISIBLE
                    goal_water.visibility = View.VISIBLE

                }
            } else if (it != null && it[0].values.isEmpty()){
                binding.lineChart.visibility = View.GONE
                binding.iconMyType.visibility = View.VISIBLE
                binding.shaperecordHint.visibility = View.VISIBLE
                binding.let {
                    recycler_foodie_sum.visibility = View.GONE
                    icon_water_goal.visibility = View.GONE
                    icon_body_water.visibility = View.GONE
                    icon_weight.visibility = View.GONE
                    icon_body_age.visibility = View.GONE
                    icon_body_fat.visibility = View.GONE
                    icon_muscle.visibility = View.GONE
                    icon_tdee.visibility = View.GONE
                    icon_calendar.visibility = View.GONE
                    number_body_age_show.visibility = View.GONE
                    number_body_fat_show.visibility = View.GONE
                    number_body_water_show.visibility = View.GONE
                    number_muscle_show.visibility = View.GONE
                    number_tdeet_show.visibility = View.GONE
                    number_weight_show.visibility = View.GONE
                    date_show.visibility = View.GONE
                    diff_carbon.visibility = View.GONE
                    diff_fruit.visibility = View.GONE
                    diff_oil.visibility = View.GONE
                    diff_protein.visibility = View.GONE
                    diff_vegetable.visibility = View.GONE
                    diff_water.visibility = View.GONE
                    goal_carbon.visibility = View.GONE
                    goal_fruit.visibility = View.GONE
                    goal_vegetable.visibility = View.GONE
                    goal_oil.visibility = View.GONE
                    goal_protein.visibility = View.GONE
                    goal_water.visibility = View.GONE
                }
            }
        })

        binding.recyclerFoodieSum.adapter = FoodieSumAdapter(viewModel)
        binding.recyclerFoodieSum.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen._1sdp).toInt(),
                true
            )
        )

        viewModel.foodieSum.observe(this, androidx.lifecycle.Observer {
            if (it.isNotEmpty()){
                (binding.recyclerFoodieSum.adapter as FoodieSumAdapter).submitList(it)
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