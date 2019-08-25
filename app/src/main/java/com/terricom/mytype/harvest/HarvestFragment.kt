package com.terricom.mytype.harvest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.terricom.mytype.databinding.FragmentFoodRecordBinding

class HarvestFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentHarvestBinding.inflate(inflater)

        return binding.root
    }
}