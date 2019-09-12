package com.terricom.mytype.dreamboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.Logger
import com.terricom.mytype.MainActivity
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.databinding.FragmentDreamBoardBinding
import com.terricom.mytype.profile.ProfileViewModel
import kotlinx.android.synthetic.main.activity_main.*

class DreamboardFragment: Fragment() {

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDreamBoardBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val pazzle = DreamboardFragmentArgs.fromBundle(arguments!!).selectedProperty


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToAchivementFragment())
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_profile
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                (activity as MainActivity).fab.visibility = View.VISIBLE
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.puzzle1.visibility = if (pazzle.position!!.contains(0)) View.INVISIBLE else View.VISIBLE
        binding.puzzle2.visibility = if (pazzle.position.contains(1)) View.INVISIBLE else View.VISIBLE
        binding.puzzle3.visibility = if (pazzle.position.contains(2)) View.INVISIBLE else View.VISIBLE
        binding.puzzle4.visibility = if (pazzle.position.contains(3)) View.INVISIBLE else View.VISIBLE
        binding.puzzle5.visibility = if (pazzle.position!!.contains(4)) View.INVISIBLE else View.VISIBLE
        binding.puzzle6.visibility = if (pazzle.position!!.contains(5)) View.INVISIBLE else View.VISIBLE
        binding.puzzle7.visibility = if (pazzle.position!!.contains(6)) View.INVISIBLE else View.VISIBLE
        binding.puzzle8.visibility = if (pazzle.position!!.contains(7)) View.INVISIBLE else View.VISIBLE
        binding.puzzle9.visibility = if (pazzle.position!!.contains(8)) View.INVISIBLE else View.VISIBLE
        binding.puzzle10.visibility = if (pazzle.position!!.contains(9)) View.INVISIBLE else View.VISIBLE
        binding.puzzle11.visibility = if (pazzle.position!!.contains(10)) View.INVISIBLE else View.VISIBLE
        binding.puzzle12.visibility = if (pazzle.position!!.contains(11)) View.INVISIBLE else View.VISIBLE
        binding.puzzle13.visibility = if (pazzle.position!!.contains(12)) View.INVISIBLE else View.VISIBLE
        binding.puzzle14.visibility = if (pazzle.position!!.contains(13)) View.INVISIBLE else View.VISIBLE
        binding.puzzle15.visibility = if (pazzle.position!!.contains(14)) View.INVISIBLE else View.VISIBLE
        binding.puzzle16.visibility = if (pazzle.position!!.contains(15)) View.INVISIBLE else View.VISIBLE
        binding.puzzle17.visibility = if (pazzle.position!!.contains(16)) View.INVISIBLE else View.VISIBLE
        binding.puzzle18.visibility = if (pazzle.position!!.contains(17)) View.INVISIBLE else View.VISIBLE
        binding.puzzle19.visibility = if (pazzle.position!!.contains(18)) View.INVISIBLE else View.VISIBLE
        binding.puzzle20.visibility = if (pazzle.position!!.contains(19)) View.INVISIBLE else View.VISIBLE
//        }


        return binding.root
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout3.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout4.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false

    }
}