package com.terricom.mytype.foodie

import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.MainActivity
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.databinding.FragmentFoodieRecordBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class FoodieFragment: Fragment() {

    val time = Calendar.getInstance().time

    private val viewModel: FoodieViewModel by lazy {
        ViewModelProviders.of(this).get(FoodieViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFoodieRecordBinding.inflate(inflater)

        binding.buttonFoodieShowInfo.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToReferenceDialog())
        }

        binding.foodsRecycler.adapter = FoodAdapter(viewModel, FoodAdapter.MyTouchListener())
        val foodList: MutableList<String> = mutableListOf("紅油抄手", "擔擔麵", "蘿蔔糕")
        (binding.foodsRecycler.adapter as FoodAdapter).submitList(foodList)
        (binding.foodsRecycler.adapter as FoodAdapter).notifyDataSetChanged()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        class MyDragListener : View.OnDragListener {

            override fun onDrag(v: View, event: DragEvent): Boolean {
                val action = event.action
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
                    }
                    else -> {
                    }
                }// do nothing
                return true
            }
        }

        binding.chosedNutrition.setOnDragListener(MyDragListener())


        return binding.root
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
        (activity as MainActivity).fab.visibility = View.VISIBLE
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false

    }

}