package com.terricom.mytype.linechart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.*
import com.terricom.mytype.calendar.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_linechart.*
import java.sql.Timestamp
import java.util.*


class LineChartFragment: Fragment() {

    private val viewModel: LineChartViewModel by lazy {
        ViewModelProviders.of(this).get(LineChartViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentLinechartBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

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
            currentPosition -= 1
            viewModel.setDate(Date(Timestamp.valueOf(App.applicationContext().getString(
                R.string.timestamp_dayend, Date().toDateFormat(
                FORMAT_YYYY_MM_DD))).time.plus(604800000L*(currentPosition))))
        }
        binding.buttonNext.setOnClickListener {
            currentPosition += 1
            viewModel.setDate(Date(Timestamp.valueOf(App.applicationContext().getString(
                R.string.timestamp_dayend, Date().toDateFormat(
                    FORMAT_YYYY_MM_DD))).time.plus(604800000L*(currentPosition))))
        }

        viewModel.recordDate.observe(this, androidx.lifecycle.Observer {
            if (it != null){
                viewModel.getThisMonth()
                viewModel.listDates.observe(this, androidx.lifecycle.Observer {
                    if (it != null && it.isNotEmpty() && it[0].values.isNotEmpty()){

                        binding.lineChart.legendArray = viewModel.chartListDate.value
                        binding.lineChart.setList(it)
                        binding.lineChart.visibility = View.VISIBLE
                        binding.iconMyType.visibility = View.GONE
                        binding.shaperecordHint.visibility = View.GONE
                        binding.blankHintLinechart.visibility = View.GONE
                        binding.let {
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

        if (!isConnected()){
            Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
            //告訴使用者網路無法使用
        }

        return binding.root
    }

}