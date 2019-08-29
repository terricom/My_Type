package com.terricom.mytype.achivement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

class AchivementFragment: Fragment() {

    private lateinit var achovementAdapter: AchivementAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentLinechartBinding.inflate(inflater)

        binding.setLifecycleOwner(this)


        // adapter
        achovementAdapter = AchivementAdapter(childFragmentManager)


        // viewPager
        viewPager = binding.viewPager
        viewPager.adapter = achovementAdapter

        // tabLayout
        val tabLayout = binding.tabLayout

        // link tabLayout with viewPager
        tabLayout.setupWithViewPager(viewPager)

        // Inflate the layout for this fragment
        return binding.root
    }
}