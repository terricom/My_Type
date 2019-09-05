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
                58.0f,
                21.0f,
                52.0f,
                20.0f,
                19.0f,
                1300.0f
                )
            ,Shape(
                Timestamp.valueOf("2018-08-31 23:49:45"),
                59.0f,
                22.0f,
                53.0f,
                30.0f,
                30.0f,
                1100.0f
            )
            )
        (binding.recyclerShape.adapter as ShapeAdapter).submitList(shapeList)

        return binding.root
    }
}