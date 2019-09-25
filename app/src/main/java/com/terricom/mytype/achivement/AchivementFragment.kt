package com.terricom.mytype.achivement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration
import java.util.*

class AchivementFragment: Fragment() {

    val viewModel: AchievementViewModel by lazy {
        ViewModelProviders.of(this).get(AchievementViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentAchivementBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        binding.recyclerShape.adapter = ShapeAdapter(viewModel, ShapeAdapter.OnClickListener{
            findNavController().navigate(NavigationDirections.navigateToShapeRecordFragment(it))
        })
        binding.recyclerShape.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen._1sdp).toInt(),
                true
            )
        )

        viewModel.recordDate.observe(this, Observer {
            viewModel.getThisMonth()
        })

        var currentPosition = 0


        viewModel.listDates.observe(this, Observer {
            if (it != null && it.isNotEmpty() && it[0].values.isNotEmpty()){
                binding.lineChart.legendArray = viewModel.fireDate.value
                binding.lineChart.setList(it)
                binding.lineChart.visibility = View.VISIBLE
                binding.iconMyType.visibility = View.GONE
                binding.shaperecordHint.visibility = View.GONE
                binding.recyclerShape.visibility = View.VISIBLE
                binding.let {
//                    icon_body_water.visibility = View.VISIBLE
//                    icon_weight.visibility = View.VISIBLE
//                    icon_body_age.visibility = View.VISIBLE
//                    icon_body_fat.visibility = View.VISIBLE
//                    icon_muscle.visibility = View.VISIBLE
//                    icon_tdee.visibility = View.VISIBLE
//                    icon_calendar.visibility = View.VISIBLE
//                    number_body_age_show.visibility = View.VISIBLE
//                    number_body_fat_show.visibility = View.VISIBLE
//                    number_body_water_show.visibility = View.VISIBLE
//                    number_muscle_show.visibility = View.VISIBLE
//                    number_tdeet_show.visibility = View.VISIBLE
//                    number_weight_show.visibility = View.VISIBLE
//                    date_show.visibility = View.VISIBLE
//                    diff_body_fat.visibility = View.VISIBLE
//                    diff_muscle.visibility = View.VISIBLE
//                    diff_weight.visibility = View.VISIBLE
//                    icon_weight_goal.visibility = View.VISIBLE
//                    icon_body_fat_goal.visibility = View.VISIBLE
//                    icon_muscle_goal.visibility = View.VISIBLE
//                    goal_body_fat.visibility = View.VISIBLE
//                    goal_muscle.visibility = View.VISIBLE
//                    goal_weight.visibility = View.VISIBLE
                }
            } else if (it != null && it[0].values.isEmpty()){
                binding.lineChart.visibility = View.GONE
                binding.recyclerShape.visibility = View.GONE
                binding.iconMyType.visibility = View.VISIBLE
                binding.shaperecordHint.visibility = View.VISIBLE
                binding.let {
//                    icon_body_water.visibility = View.GONE
//                    icon_weight.visibility = View.GONE
//                    icon_body_age.visibility = View.GONE
//                    icon_body_fat.visibility = View.GONE
//                    icon_muscle.visibility = View.GONE
//                    icon_tdee.visibility = View.GONE
//                    icon_calendar.visibility = View.GONE
//                    number_body_age_show.visibility = View.GONE
//                    number_body_fat_show.visibility = View.GONE
//                    number_body_water_show.visibility = View.GONE
//                    number_muscle_show.visibility = View.GONE
//                    number_tdeet_show.visibility = View.GONE
//                    number_weight_show.visibility = View.GONE
//                    date_show.visibility = View.GONE
//                    diff_body_fat.visibility = View.GONE
//                    diff_muscle.visibility = View.GONE
//                    diff_weight.visibility = View.GONE
//                    icon_weight_goal.visibility = View.GONE
//                    icon_body_fat_goal.visibility = View.GONE
//                    icon_muscle_goal.visibility = View.GONE
//                    goal_body_fat.visibility = View.GONE
//                    goal_muscle.visibility = View.GONE
//                    goal_weight.visibility = View.GONE
                }
            }
        })
        viewModel.fireShape.observe(this, Observer {
            Logger.i("viewModel.fireShape.observe = $it")
            if (it.isNotEmpty()){
            (binding.recyclerShape.adapter as ShapeAdapter).submitList(it)
            }
        })

        val currentCalendar = Calendar.getInstance()

        binding.toBack.setOnClickListener {
            currentCalendar.time = viewModel.recordDate.value
            currentCalendar.add(Calendar.MONTH, -1)
            currentPosition = currentPosition-1
            viewModel.setDate(currentCalendar.time)
            viewModel.getThisMonth()
        }
        binding.toNext.setOnClickListener {
            currentCalendar.time = viewModel.recordDate.value
            currentCalendar.add(Calendar.MONTH, 1)
            viewModel.setDate(currentCalendar.time)
            viewModel.getThisMonth()
//            binding.viewPager2.setCurrentItem( currentPosition.plus(1), true)
        }

        return binding.root
    }
}