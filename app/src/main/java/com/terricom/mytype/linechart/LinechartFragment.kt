package com.terricom.mytype.linechart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

class LinechartFragment: Fragment() {

    private lateinit var linechartAdapter: LinechartAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentLinechartBinding.inflate(inflater)

        binding.setLifecycleOwner(this)


        // adapter
        linechartAdapter = LinechartAdapter(childFragmentManager)


        // viewPager
        viewPager = binding.viewPager
        viewPager.adapter = linechartAdapter

        // tabLayout
        val tabLayout = binding.tabLayout

        // link tabLayout with viewPager
        tabLayout.setupWithViewPager(viewPager)

        // Inflate the layout for this fragment
        return binding.root
    }

}