package com.terricom.mytype.linechart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.terricom.mytype.Logger
import java.text.SimpleDateFormat
import java.util.*


class LinechartFragment: Fragment() {

    val currentDateTime = Calendar.getInstance()
    val thisWeek = mutableListOf<String>()
    val weeek = arrayListOf<String>()
    private val viewModel: LinechartViewModel by lazy {
        ViewModelProviders.of(this).get(LinechartViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = com.terricom.mytype.databinding.FragmentLinechartBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        var currentPosition = binding.viewPager2.currentItem

        binding.viewPager2.adapter = LinechartAdapter(viewModel)
        (binding.viewPager2.adapter as LinechartAdapter).notifyDataSetChanged()
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)

            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
                viewModel.clearData()
                viewModel.setCurrentPosition(position)
                Logger.i("Selected_Page , position.toString() = $position")
                viewModel.newFireBack()
                viewModel.setDate(Date(Date().time.plus(604800000L*(currentPosition-20))))
                viewModel.getThisMonth()
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)

            }
        })


        if (viewModel.date.value == sdf.format(Date())){
            binding.viewPager2.setCurrentItem(20, true)
        }
        binding.toBack.setOnClickListener {
            viewModel.newFireBack()
            viewModel.setDate(Date(Date().time.plus(604800000L*(currentPosition-20))))
            binding.viewPager2.setCurrentItem( currentPosition.minus(1), true)

        }
        binding.toNext.setOnClickListener {
            viewModel.newFireBack()
            viewModel.setDate(Date(Date().time.plus(604800000L*(currentPosition-20))))
            binding.viewPager2.setCurrentItem( currentPosition.plus(1), true)
        }

//        viewModel.date.observe(this, androidx.lifecycle.Observer {
//            viewModel.getThisMonth()
//        })

        return binding.root
    }

    private fun getThisWeek(){
        val currentMonth = currentDateTime.get(Calendar.MONTH) + 1
        val lastDayOfLastMonth = getLastMonthLastDate()
        val currentDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        for (i in currentDay-6 until  currentDay ){
            if (i>-1){
                weeek.add("$currentMonth/$i")
                thisWeek.add("$currentMonth/$i")
            }else {
                weeek.add("${currentMonth-1}/${lastDayOfLastMonth+i}")
                thisWeek.add("${currentMonth-1}/${lastDayOfLastMonth+i}")
            }
        }
        thisWeek.add("${currentMonth}/${currentDay}")

    }

    fun getLastMonthLastDate(): Int {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)

        val max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, max)

        return calendar.get(Calendar.DAY_OF_MONTH)
    }




}