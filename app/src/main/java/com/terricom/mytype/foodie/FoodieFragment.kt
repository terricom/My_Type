package com.terricom.mytype.foodie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.terricom.mytype.databinding.FragmentAddFoodieBinding

class FoodieFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentAddFoodieBinding.inflate(inflater)

        return binding.root
    }

}