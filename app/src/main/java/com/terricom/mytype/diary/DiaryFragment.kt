package com.terricom.mytype.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.databinding.FragmentDiaryBinding


class DiaryFragment: Fragment() {

    private val viewModel: DiaryViewModel by lazy {
        ViewModelProviders.of(this).get(DiaryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDiaryBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerView.adapter = FoodieAdapter(viewModel)
        viewModel.fireSleep.observe(this, Observer {
            (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
        })
        viewModel.fireShape.observe(this, Observer {
            (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
        })
        viewModel.fireFoodie.observe(this, Observer {
            (binding.recyclerView.adapter as FoodieAdapter).addHeaderAndSubmitList(it)
            (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
        })


        return binding.root
    }

}