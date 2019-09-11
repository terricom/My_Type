package com.terricom.mytype.achivement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration

class AchivementFragment: Fragment() {

    val viewModel: AchievementViewModel by lazy {
        ViewModelProviders.of(this).get(AchievementViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentAchivementBinding.inflate(inflater)
        binding.viewMoel = viewModel
        binding.setLifecycleOwner(this)

        binding.recyclerShape.adapter = ShapeAdapter(viewModel)
        binding.recyclerShape.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen._1sdp).toInt(),
                true
            )
        )
        viewModel.fireShape.observe(this, Observer {
            if (it.size > 0){}
            (binding.recyclerShape.adapter as ShapeAdapter).submitList(it)
        })


        return binding.root
    }
}