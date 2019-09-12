package com.terricom.mytype.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.calendar.CalendarFragment
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.databinding.FragmentDiaryBinding
import java.text.SimpleDateFormat


class DiaryFragment: Fragment(), CalendarFragment.EventBetweenCalendarAndFragment
{

    private val viewModel: DiaryViewModel by lazy {
        ViewModelProviders.of(this).get(DiaryViewModel::class.java)
    }
    private lateinit var binding :FragmentDiaryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDiaryBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.diaryCalendar.setEventHandler(this)
        binding.diaryCalendar.updateCalendar()

        binding.recyclerView.adapter = FoodieAdapter(viewModel)
        viewModel.fireSleep.observe(this, Observer {
            (binding.recyclerView.adapter as FoodieAdapter).addHeaderAndSubmitList(viewModel.fireFoodie.value)
            (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
        })
        viewModel.fireShape.observe(this, Observer {
            (binding.recyclerView.adapter as FoodieAdapter).addHeaderAndSubmitList(viewModel.fireFoodie.value)
            (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
        })
        viewModel.fireFoodie.observe(this, Observer {
            if (it.size > 0){
                Logger.i("viewModel.fireFoodie.observe = $it")
                binding.diaryHintAddFoodie.visibility = View.GONE
                binding.iconMyType.visibility = View.GONE
            }
            (binding.recyclerView.adapter as FoodieAdapter).addHeaderAndSubmitList(it)
            (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
        })

        binding.recyclerView.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen.elevation_all).toInt(),
                true
            )
        )

        viewModel.calendarClicked.observe(this, Observer {
            Logger.i("viewModel.calendarClicked.observe =$it")
            if (it == true){
                binding.buttonSaveCalendar.setOnClickListener {
                    binding.buttonExpandArrow.animate().rotation(180f).start()
                    binding.diaryCalendar.visibility = View.GONE
                    if (viewModel.fireFoodie.value!!.size <= 0){
                        binding.diaryHintAddFoodie.visibility = View.VISIBLE

                    }
                    viewModel.calendarClickedAgain()
                }
            }else if (it == false){
                binding.buttonSaveCalendar.setOnClickListener {
                    binding.buttonExpandArrow.animate().rotation(0f).start()
                    binding.diaryCalendar.visibility = View.VISIBLE
                    binding.diaryHintAddFoodie.visibility = View.INVISIBLE
                    viewModel.calendarClicked()
                }

            }

        })

//        if (viewModel.calendarClicked.value == true){
//            viewModel.calendarClickedAgain()
//            binding.buttonSaveCalendar.setOnClickListener {
//                binding.buttonExpandArrow.animate().rotation(resources.getDimension(R.dimen.diary_up_side_down))
//                binding.diaryCalendar.visibility = View.GONE
//            }
//        }else {
//            binding.buttonSaveCalendar.setOnClickListener {
//                binding.buttonExpandArrow.animate().rotation(resources.getDimension(R.dimen.diary_up_side_down))
//                binding.diaryCalendar.visibility = View.VISIBLE
//
//            }
//
//        }

        viewModel.date.observe(this, Observer {
            Logger.i("viewModel.date.observe === $it")
            val sdf = SimpleDateFormat("yyyy-MM-dd")

            binding.diaryDate.text = sdf.format(it)
        })

//        if ( binding.diaryDate.text == ""){
//            val sdf = SimpleDateFormat("yyyy-MM-dd")
//            val currentDate = sdf.format(Date())
//            viewModel.filterdate(currentDate)
//        }


        return binding.root
    }

    override fun onCalendarNextPressed() {
        binding.diaryCalendar.updateCalendar()
    }

    override fun onCalendarPreviousPressed() {
        binding.diaryCalendar.updateCalendar()
    }


}