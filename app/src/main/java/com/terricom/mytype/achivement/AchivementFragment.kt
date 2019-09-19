package com.terricom.mytype.achivement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration

class AchivementFragment: Fragment() {

    val viewModel: AchievementViewModel by lazy {
        ViewModelProviders.of(this).get(AchievementViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentAchivementBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        binding.recyclerShape.adapter = ShapeAdapter(viewModel)
        binding.recyclerShape.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen._1sdp).toInt(),
                true
            )
        )

        viewModel.recordDate.observe(this, Observer {
            viewModel.getThisMonth()
        })


        viewModel.listDates.observe(this, Observer {
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
        viewModel.fireShape.observe(this, Observer {
            Logger.i("viewModel.fireShape.observe = $it")
            if (it.isNotEmpty()){
            (binding.recyclerShape.adapter as ShapeAdapter).submitList(it)
            }
        })


        return binding.root
    }
}