package com.terricom.mytype.linechart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.terricom.mytype.databinding.LinechartMonthBinding

class MonthLinechart : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = LinechartMonthBinding.inflate(inflater)

        return binding.root
    }

}