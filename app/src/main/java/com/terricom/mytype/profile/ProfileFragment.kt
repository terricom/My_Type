package com.terricom.mytype.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.data.Pazzle

class ProfileFragment: Fragment() {

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentProfileBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner= this
        val pazzleMock = Pazzle(
            listOf(0,4,6,8),
            ""
        )
//        val pazzleMock2 = Pazzle(
//            listOf(10,14,16,18),
//            ""
//        )
        binding.recyclerPuzzle.adapter = PazzleAdapter(viewModel, PazzleAdapter.OnClickListener{
            viewModel.setPazzle(it)
            findNavController().navigate(NavigationDirections.navigateToDreamBoardFragment(it))
        })
        binding.recyclerPuzzle.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen.elevation_all).toInt(),
                true
            )
        )
        (binding.recyclerPuzzle.adapter as PazzleAdapter).submitList(listOf(pazzleMock))

        return binding.root
    }
}