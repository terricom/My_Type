package com.terricom.mytype.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

class ProfileFragment: Fragment() {

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentProfileBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner= this

        binding.buttonProfileGoalSave.setOnClickListener {
            val inputDate = binding.profileTitleMyGoalDate.text.toString()
            viewModel.date.value = inputDate
            val goal = binding.profileMyGoal.text.toString()
            viewModel.goal.value = goal

        }

        binding.buttonProfileMenuSave.setOnClickListener {
            viewModel.addMenu()
        }

        return binding.root
    }
}