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
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class LinechartFragment: Fragment() {

    val currentDateTime = Calendar.getInstance()
    val thisWeek = mutableListOf<String>()
    private val week = arrayListOf<String>()
    private val viewModel: LinechartViewModel by lazy {
        ViewModelProviders.of(this).get(LinechartViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentLinechartBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        var currentPosition = 0

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

        binding.buttonBack.setOnClickListener {
            currentPosition = currentPosition-1
            viewModel.setDate(Date(Timestamp.valueOf("${sdf.format(Date())} 23:59:59.999999999").time.plus(604800000L*(currentPosition))))
        }
        binding.buttonNext.setOnClickListener {
            currentPosition = currentPosition+1
            viewModel.setDate(Date(Timestamp.valueOf("${sdf.format(Date())} 23:59:59.999999999").time.plus(604800000L*(currentPosition))))
        }

        viewModel.recordDate.observe(this, androidx.lifecycle.Observer {
            if (it != null){
                viewModel.getThisMonth()
                viewModel.listDates.observe(this, androidx.lifecycle.Observer {
                    if (it != null && it.isNotEmpty() && it[0].values.isNotEmpty()){

                        binding.lineChart.legendArray = viewModel.fireDate.value
                        binding.lineChart.setList(it)
                        binding.lineChart.visibility = View.VISIBLE
                        binding.iconMyType.visibility = View.GONE
                        binding.shaperecordHint.visibility = View.GONE
                        binding.blankHintLinechart.visibility = View.GONE
                        binding.let {
                            icon_water_goal_placeholder.visibility = View.VISIBLE
                            icon_fruit_placeholder.visibility = View.VISIBLE
                            icon_vegetable_goal_placeholder.visibility = View.VISIBLE
                            icon_oil_goal_placeholder.visibility = View.VISIBLE
                            icon_protein_goal_placeholder.visibility = View.VISIBLE
                            icon_carbon_placeholder.visibility = View.VISIBLE
                            goal_water_placeholder.visibility = View.VISIBLE
                            goal_fruit_placeholder.visibility = View.VISIBLE
                            goal_vegetable_placeholder.visibility = View.VISIBLE
                            goal_oil_placeholder.visibility = View.VISIBLE
                            goal_protein_placeholder.visibility = View.VISIBLE
                            goal_carbon_placeholder.visibility = View.VISIBLE
                            recycler_foodie_sum_placeholder.visibility = View.VISIBLE
                            recycler_foodie_sum.visibility = View.VISIBLE
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
                            icon_water_goal.visibility = View.VISIBLE
                            icon_vegetable_goal.visibility = View.VISIBLE
                            icon_fruit.visibility = View.VISIBLE
                            icon_oil_goal.visibility = View.VISIBLE
                            icon_protein_goal.visibility = View.VISIBLE
                            icon_carbon.visibility = View.VISIBLE
                        }
                    } else if (it != null && it[0].values.isEmpty()){
                        binding.lineChart.visibility = View.GONE
                        binding.iconMyType.visibility = View.VISIBLE
                        binding.shaperecordHint.visibility = View.VISIBLE
                        binding.blankHintLinechart.visibility = View.VISIBLE
                        binding.let {
                            icon_water_goal_placeholder.visibility = View.GONE
                            icon_fruit_placeholder.visibility = View.GONE
                            icon_vegetable_goal_placeholder.visibility = View.GONE
                            icon_oil_goal_placeholder.visibility = View.GONE
                            icon_protein_goal_placeholder.visibility = View.GONE
                            icon_carbon_placeholder.visibility = View.GONE
                            goal_water_placeholder.visibility = View.GONE
                            goal_fruit_placeholder.visibility = View.GONE
                            goal_vegetable_placeholder.visibility = View.GONE
                            goal_oil_placeholder.visibility = View.GONE
                            goal_protein_placeholder.visibility = View.GONE
                            goal_carbon_placeholder.visibility = View.GONE
                            recycler_foodie_sum_placeholder.visibility = View.INVISIBLE
                            recycler_foodie_sum.visibility = View.GONE
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
                            icon_water_goal.visibility = View.GONE
                            icon_vegetable_goal.visibility = View.GONE
                            icon_fruit.visibility = View.GONE
                            icon_oil_goal.visibility = View.GONE
                            icon_protein_goal.visibility = View.GONE
                            icon_carbon.visibility = View.GONE
                        }
                    }
                })
            }
        })


        return binding.root
    }

}