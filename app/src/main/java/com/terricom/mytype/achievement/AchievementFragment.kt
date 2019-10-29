package com.terricom.mytype.achievement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.App
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.tools.getVmFactory
import com.terricom.mytype.tools.isConnected
import kotlinx.android.synthetic.main.fragment_achievement.*
import java.util.*

class AchievementFragment: Fragment() {

    private val viewModel by viewModels<AchievementViewModel> { getVmFactory() }
    private val currentCalendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentAchievementBinding.inflate(inflater)

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

            currentCalendar.time = viewModel.currentDate.value
            currentCalendar.add(Calendar.MONTH, -1)

            viewModel.setCurrentDate(currentCalendar.time)
            viewModel.getAndSetDataShapeOfThisMonth()
        }

        binding.buttonNext.setOnClickListener {

            currentCalendar.time = viewModel.currentDate.value
            currentCalendar.add(Calendar.MONTH, 1)

            viewModel.setCurrentDate(currentCalendar.time)
            viewModel.getAndSetDataShapeOfThisMonth()
        }

        viewModel.currentDate.observe(this, Observer {

            viewModel.getAndSetDataShapeOfThisMonth()

            viewModel.dataShapeFromFirebase.observe(this, Observer {

                if (!it.isNullOrEmpty()){
                    (binding.recyclerShape.adapter as ShapeAdapter).submitList(it)
                }
            })

            viewModel.listOfChartEntities.observe(this, Observer {

                it?.let {
                    if (it.isNotEmpty() && it[0].values.isNotEmpty()){

                        binding.lineChart.setList(it)

                        binding.let {
                            lineChart.legendArray = viewModel.recordedDatesOfThisMonth.value
                            lineChart.visibility = View.VISIBLE
                            icon_my_type.visibility = View.GONE
                            recycler_shape.visibility = View.VISIBLE
                            shaperecord_hint.visibility = View.GONE
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

                    } else if (it[0].values.isEmpty()){

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
                }

            })
        })

        if (!isConnected()){
            Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
            //告訴使用者網路無法使用
        }

        return binding.root
    }
}