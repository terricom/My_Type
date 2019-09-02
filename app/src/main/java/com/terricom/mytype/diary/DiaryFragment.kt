package com.terricom.mytype.diary

import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnDragListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.R
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.databinding.FragmentDiaryBinding
import java.sql.Timestamp


class DiaryFragment: Fragment() {

    private val viewModel: DiaryViewModel by lazy {
        ViewModelProviders.of(this).get(DiaryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDiaryBinding.inflate(inflater)
        binding.setLifecycleOwner(this)

//        binding.nutritionRecycler.adapter = NutritionAdapter(viewModel
//            , NutritionAdapter.MyTouchListener()
//        )
//        val nutritionList: MutableList<String> = mutableListOf("葉黃素", "維他命", "葡萄糖")
//        (binding.nutritionRecycler.adapter as NutritionAdapter).submitList(nutritionList)
//        (binding.nutritionRecycler.adapter as NutritionAdapter).notifyDataSetChanged()


//            if (viewModel.addNutrition.value != null) {
//            nutritionList.add(viewModel.addNutrition.value as String)
//        }

        class MyDragListener : OnDragListener {

            override fun onDrag(v: View, event: DragEvent): Boolean {
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                    }
                    DragEvent.ACTION_DROP -> {
                        // Dropped, reassign View to ViewGroup
                        val view = event.localState as View
                        val owner = view.parent as ViewGroup
                        owner.removeView(view)
                        val container = v as LinearLayout
                        container.addView(view)
                        view.visibility = View.VISIBLE
                        viewModel.dragToList("${view.findViewById<TextView>(R.id.nutrition).text}")
                    }
                    else -> {
                    }
                }// do nothing
                return true
            }
        }

//        binding.chosedNutrition.setOnDragListener(MyDragListener())

        binding.recyclerView.adapter = FoodieAdapter(viewModel)
        val foodieList: MutableList<Foodie> = mutableListOf(
            Foodie(
                Timestamp.valueOf("2018-10-16 11:49:45"),
                "URL",
                listOf("沙拉" ,"水果") ,
                8,
                7,
                6,
                5,
                4,
                3,
                listOf("葉黃素","氨基酸")
                )
            , Foodie(
                Timestamp.valueOf("2019-09-31 11:00:00"),
                "URL",
                listOf("生菜" ,"花椰菜") ,
                8,
                7,
                6,
                5,
                4,
                3,
                listOf("膠原蛋白","Ｂ群")

            )
            , Foodie(
                Timestamp.valueOf("2019-08-31 20:00:00"),
                "URL",
                listOf("麻糬" ,"豆漿") ,
                8,
                7,
                6,
                5,
                4,
                3,
                listOf("高蛋白","老虎牙子")


            )
        )
        (binding.recyclerView.adapter as FoodieAdapter).submitList(foodieList)


        return binding.root
    }

}