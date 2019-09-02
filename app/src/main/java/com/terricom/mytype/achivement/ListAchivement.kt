package com.terricom.mytype.achivement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.data.Shape
import java.sql.Timestamp

class ListAchivement: Fragment() {

    private val viewModel: AchievementViewModel by lazy {
        ViewModelProviders.of(this).get(AchievementViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.AchivementListBinding.inflate(inflater)
        binding.setLifecycleOwner(this)

        binding.recyclerShape.adapter = ShapeAdapter(viewModel)
        val shapeList: MutableList<Shape> = mutableListOf(
            Shape(
                Timestamp.valueOf("2018-10-16 11:49:45"),
                58,
                21,
                52,
                20,
                19,
                1300
                )
            ,Shape(
                Timestamp.valueOf("2018-08-31 23:49:45"),
                59,
                22,
                53,
                30,
                30,
                1100
            )
            )
        (binding.recyclerShape.adapter as ShapeAdapter).submitList(shapeList)

        return binding.root
    }
}