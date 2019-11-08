package com.terricom.mytype.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.App
import com.terricom.mytype.MessageDialog
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.calendar.CalendarComponentLayout
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.databinding.FragmentDiaryBinding
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.getVmFactory
import com.terricom.mytype.tools.isConnected
import com.terricom.mytype.tools.toDateFormat


class DiaryFragment: Fragment(), CalendarComponentLayout.EventBetweenCalendarAndFragment
{

    private val viewModel by viewModels<DiaryViewModel> { getVmFactory() }
    private lateinit var binding :FragmentDiaryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDiaryBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerView.adapter = FoodieAdapter(
            viewModel, FoodieAdapter.OnClickListener{foodie ->

            findNavController().navigate(NavigationDirections.navigateToFoodieFragment(foodie))
        })

        viewModel.getGoal()

        viewModel.isGetPuzzle.observe(this, Observer {

            when (it){

                false ->
                    if (UserManager.getPuzzleNewUser == "2"){ //首次加入才會跳通知（新用戶）

                        this.findNavController().navigate(NavigationDirections.navigateToMessageDialog(
                            MessageDialog.MessageType.GET_PUZZLE.apply {
                                value.message = App.applicationContext().resources.getString(R.string.diary_puzzle_check_new)
                            })
                        )
                        //只有第一次跳 dialog
                        UserManager.getPuzzleNewUser = UserManager.getPuzzleNewUser.toString()
                            .toInt().plus(1).toString()
                    }

                true -> {
                    if (UserManager.getPuzzleOldUser == "2"){ //當天的首篇食記才會跳通知（老用戶）

                        this.findNavController().navigate(NavigationDirections.navigateToMessageDialog(
                            MessageDialog.MessageType.GET_PUZZLE.apply {
                                value.message = App.applicationContext().resources.getString(R.string.diary_puzzle_check_old)
                            }))
                        //只有第一次跳 dialog
                        UserManager.getPuzzleOldUser = UserManager.getPuzzleOldUser.toString().toInt().plus(1).toString()
                    }

                }
                else -> {
                }
            }
        })

        viewModel.isCallDeleteAction.observe(this, Observer {
            if (it == true){
                viewModel.getAndSetFoodieShapeSleepToday()

            }
        })

        viewModel.dataFoodieFromFirebase.observe(this, Observer {

            it?.let {
                (binding.recyclerView.adapter as FoodieAdapter).diarySubmitList(it)
                (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
            }
        })

        viewModel.date.observe(this, Observer { it ->

            if (it != null){
                viewModel.clearDataShapeFromFirebase()
                viewModel.clearDataSleepFromFirebase()
                viewModel.getAndSetFoodieShapeSleepToday()
                binding.diaryDate.text = it.toDateFormat(FORMAT_YYYY_MM_DD)

                binding.diaryCalendar.setEventHandler(this)
                binding.diaryCalendar.setCurrentDate(binding.diaryCalendar.selectedDayOut)
                binding.diaryCalendar.getThisMonth()
                binding.diaryCalendar.recordedDate.observe(this, Observer {

                    it?.let {
                        if (it != listOf("")){
                            binding.diaryCalendar.updateCalendar()
                        }
                    }
                })
                viewModel.dataFoodieFromFirebase.observe(this, Observer {

                    it?.let {
                        (binding.recyclerView.adapter as FoodieAdapter).diarySubmitList(it)
                        (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
                    }
                })
            }
        })

        binding.recyclerView.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen.elevation_all).toInt(),
                true
            )
        )

        viewModel.isCalendarClicked.observe(this, Observer {

            when (it){

                true ->
                    binding.diaryDate.setOnClickListener {
                        binding.buttonExpandArrow.animate().rotation(0f).start()
                        binding.diaryCalendar.animate().translationY(-resources.getDimension(R.dimen.standard_305)).start()
                        binding.diaryCalendar.visibility = View.GONE
                        viewModel.setCurrentDate(binding.diaryCalendar.selectedDayOut)
                        viewModel.calendarClickedAgain()
                    }
                false ->
                    binding.diaryDate.setOnClickListener {
                        binding.buttonExpandArrow.animate().rotation(180f).start()
                        binding.diaryCalendar.animate().translationY(resources.getDimension(R.dimen.standard_0)).start()
                        binding.diaryCalendar.visibility = View.VISIBLE
                        binding.diaryCalendar.getThisMonth()
                        viewModel.calendarClicked()
                    }
            }
        })

        viewModel.listQueryFoodieResult.observe(this, Observer {

            it?.let {
                findNavController().navigate(NavigationDirections.navigateToQueryFragment(it))
            }
        })

        if (!isConnected()){
            Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
            //告訴使用者網路無法使用
        }

        viewModel.setCurrentDate(binding.diaryCalendar.selectedDayOut)

        return binding.root
    }

    override fun onCalendarNextPressed() {

        binding.diaryCalendar.setCurrentDate(binding.diaryCalendar.selectedDayOut)
        binding.diaryCalendar.getThisMonth()
        binding.diaryCalendar.recordedDate.observe(this, Observer {
            binding.diaryCalendar.updateCalendar()
        })
    }

    override fun onCalendarPreviousPressed() {

        binding.diaryCalendar.setCurrentDate(binding.diaryCalendar.selectedDayOut)
        binding.diaryCalendar.getThisMonth()
        binding.diaryCalendar.recordedDate.observe(this, Observer {
            binding.diaryCalendar.updateCalendar()
        })
    }


}