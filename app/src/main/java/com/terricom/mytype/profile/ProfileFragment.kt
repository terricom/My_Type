package com.terricom.mytype.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.data.Pazzle
import com.terricom.mytype.data.PuzzleImg
import com.terricom.mytype.databinding.FragmentProfileBinding



class ProfileFragment: Fragment() {

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner= this
        val pazzleMock = Pazzle(
            listOf(0,4,6,8),
            PuzzleImg.DRINKWATER.value
        )
        val pazzleMock2 = Pazzle(
            listOf(10,14,3),
            PuzzleImg.GOODOIL.value
        )

//        viewModel.setPazzle(listOf(pazzleMock, pazzleMock2))

        val linearSnapHelper = LinearSnapHelper().apply {
            attachToRecyclerView(binding.recyclerPuzzle)
        }

        binding.recyclerPuzzle.adapter = PazzleAdapter(viewModel)
        binding.recyclerPuzzle.setOnScrollChangeListener { _, _, _, _, _ ->
            viewModel.onGalleryScrollChange(
                binding.recyclerPuzzle.layoutManager,
                linearSnapHelper
            )
        }

        (binding.recyclerGoal.getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations = false
        binding.recyclerGoal.setHasFixedSize(true)

        binding.profileGoalSettingReference.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToGoalSettingDialog())
        }

        viewModel.pazzle.value?.let {pazzleList ->
            binding.recyclerPuzzle
                .scrollToPosition(pazzleList.size * 100)
        }

        viewModel.goal.observe(this, Observer {
            if (it.isNotEmpty()){
                binding.recyclerGoal.adapter = GoalAdapter(viewModel, GoalAdapter.OnClickListener{
                    findNavController().navigate(NavigationDirections.navigateToGoalSettingFragment(it))

                })
                (binding.recyclerGoal.adapter as GoalAdapter).submitList(it)
            }
        })

        binding.recyclerPuzzle.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen.elevation_all).toInt(),
                true
            )
        )

        return binding.root
    }

}