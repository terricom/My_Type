package com.terricom.mytype.achivement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_achivement.*
import java.util.*

class AchivementFragment: Fragment() {

    val viewModel: AchievementViewModel by lazy {
        ViewModelProviders.of(this).get(AchievementViewModel::class.java)
    }
    private val currentCalendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentAchivementBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerShape.adapter = ShapeAdapter(viewModel, ShapeAdapter.OnClickListener{
            findNavController().navigate(NavigationDirections.navigateToShapeRecordFragment(it))
        })

        binding.recyclerShape.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen._1sdp).toInt(),
                true
            )
        )

        binding.buttonBack.setOnClickListener {

            currentCalendar.time = viewModel.recordDate.value
            currentCalendar.add(Calendar.MONTH, -1)

            viewModel.setDate(currentCalendar.time)
            viewModel.getThisMonth()
        }

        binding.buttonNext.setOnClickListener {

            currentCalendar.time = viewModel.recordDate.value
            currentCalendar.add(Calendar.MONTH, 1)

            viewModel.setDate(currentCalendar.time)
            viewModel.getThisMonth()
        }

        viewModel.recordDate.observe(this, Observer {

            viewModel.getThisMonth()

            viewModel.fireShape.observe(this, Observer {

                if (it.isNotEmpty()){

                    (binding.recyclerShape.adapter as ShapeAdapter).submitList(it)
                }
            })

            viewModel.listDates.observe(this, Observer {

                if (it != null && it.isNotEmpty() && it[0].values.isNotEmpty()){

                    binding.lineChart.setList(it)

                    binding.let {
                        lineChart.legendArray = viewModel.fireDate.value
                        lineChart.visibility = View.VISIBLE
                        icon_my_type.visibility = View.GONE
                        shaperecord_hint.visibility = View.GONE
                        recycler_shape.visibility = View.VISIBLE
                        blank_hint_achievement.visibility = View.INVISIBLE
                        diff_body_fat.visibility = View.VISIBLE
                        diff_muscle.visibility = View.VISIBLE
                        diff_weight.visibility = View.VISIBLE
                        icon_weight_goal.visibility = View.VISIBLE
                        icon_body_fat_goal.visibility = View.VISIBLE
                        icon_muscle_goal.visibility = View.VISIBLE
                        goal_body_fat.visibility = View.VISIBLE
                        goal_muscle.visibility = View.VISIBLE
                        goal_weight.visibility = View.VISIBLE
                    }

                } else if (it != null && it[0].values.isEmpty()){

                    binding.let {
                        lineChart.visibility = View.GONE
                        recycler_shape.visibility = View.GONE
                        icon_my_type.visibility = View.VISIBLE
                        shaperecord_hint.visibility = View.VISIBLE
                        blank_hint_achievement.visibility = View.VISIBLE
                        diff_body_fat.visibility = View.GONE
                        diff_muscle.visibility = View.GONE
                        diff_weight.visibility = View.GONE
                        icon_weight_goal.visibility = View.GONE
                        icon_body_fat_goal.visibility = View.GONE
                        icon_muscle_goal.visibility = View.GONE
                        goal_body_fat.visibility = View.GONE
                        goal_muscle.visibility = View.GONE
                        goal_weight.visibility = View.GONE
                    }
                }
            })


        })


        return binding.root
    }
}