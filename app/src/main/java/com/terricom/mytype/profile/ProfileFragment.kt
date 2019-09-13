package com.terricom.mytype.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearSnapHelper
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.data.Pazzle
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
            "https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fplaceholders%2Fplaceholder_1.png?alt=media&token=690d7b59-a06e-44a2-9c9e-1bffccccf74b"
        )
        val pazzleMock2 = Pazzle(
            listOf(10,14,16,18),
            "https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fplaceholders%2Fplaceholder_1.png?alt=media&token=690d7b59-a06e-44a2-9c9e-1bffccccf74b"
        )

        viewModel.setPazzle(listOf(pazzleMock, pazzleMock2))

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

        viewModel.pazzle.value?.let {pazzleList ->
            binding.recyclerPuzzle
                .scrollToPosition(pazzleList.size * 100)
        }


        binding.recyclerPuzzle.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen.elevation_all).toInt(),
                true
            )
        )

        return binding.root
    }

}