package com.terricom.mytype.goalsetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.*
import com.terricom.mytype.databinding.FragmentGoalSettingBinding
import com.terricom.mytype.shaperecord.ShapeCalendarFragment
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.tools.getVmFactory
import com.terricom.mytype.tools.isConnected
import kotlinx.android.synthetic.main.fragment_shape_record_calendar.view.*

class GoalSettingFragment: Fragment(), ShapeCalendarFragment.EventBetweenCalendarAndFragment {

    private val viewModel by viewModels<GoalSettingViewModel> { getVmFactory() }

    private lateinit var binding : FragmentGoalSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val goal = GoalSettingFragmentArgs.fromBundle(arguments!!).selectedProperty

        binding = FragmentGoalSettingBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.goal = goal

        if (goal.deadline != null){
            binding.shapeRecordTitle.text = App.applicationContext().getString(R.string.goal_title_adjust_goal)
            binding.smartCustomCalendar.toNext.visibility = View.VISIBLE
            viewModel.setDate(goal.deadline)
            viewModel.water.value = goal.water
            viewModel.fruit.value = goal.fruit
            viewModel.protein.value = goal.protein
            viewModel.vegetable.value = goal.vegetable
            viewModel.oil.value = goal.oil
            viewModel.carbon.value = goal.carbon
            viewModel.weight.value = goal.weight
            viewModel.bodyFat.value = goal.bodyFat
            viewModel.muscle.value = goal.muscle
            viewModel.cheerUp.value = goal.cheerUp

            binding.smartCustomCalendar.setEventHandler(this)
            binding.smartCustomCalendar.getAndSetDataShape()
            binding.smartCustomCalendar.setSelectDate(goal.deadline)
            binding.smartCustomCalendar.selectDateOut.observe(this, Observer {
                Logger.i("binding.smartCustomCalendar.selectDateOut.observe = $it")
                viewModel.setDate(it)
            })


            binding.smartCustomCalendar.isSelected = true
            binding.smartCustomCalendar.recordedDate.observe(this, Observer {
                binding.smartCustomCalendar.updateCalendar()
            })
            binding.textGoalSave.text = App.applicationContext().getString(R.string.add_new_confirm)
            binding.buttonGoalSettingSave.setOnClickListener {

                if (isConnected()){

                    it.background = App.applicationContext().getDrawable(R.color.colorSecondary)
                    viewModel.addGoal(goal.docId)
                } else {
                    Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
                }


            }

        }else {

            binding.smartCustomCalendar.setEventHandler(this)
            binding.smartCustomCalendar.toNext.visibility = View.VISIBLE
            binding.smartCustomCalendar.getAndSetDataShape()
            binding.smartCustomCalendar.recordedDate.observe(this, Observer {
                binding.smartCustomCalendar.updateCalendar()
            })


            binding.buttonGoalSettingSave.setOnClickListener{

                if (isConnected()) {

                    if ((viewModel.water.value ?: 0f).plus(viewModel.fruit.value ?: 0f)
                            .plus(viewModel.vegetable.value ?: 0f)
                            .plus(viewModel.oil.value ?: 0f)
                            .plus(viewModel.protein.value ?: 0f)
                            .plus(viewModel.carbon.value ?: 0f)
                            .plus(viewModel.weight.value ?: 0f)
                            .plus(viewModel.muscle.value ?: 0f)
                            .plus(viewModel.bodyFat.value ?: 0f) != 0f){

                    viewModel.addGoal("")
                    }
                    else {
                        Toast.makeText(App.applicationContext(), App.applicationContext().getText(R.string.goalsetting_input_hint), Toast.LENGTH_SHORT).show()
                    }
                }else{

                    Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.goal.observe(this, Observer {
            viewModel.deadline = (it[0]).deadline
        })

        viewModel.isAddGoal2Firebase.observe(this, Observer {
            if (it == true){
                binding.buttonGoalSettingSave.background = App.applicationContext().getDrawable(R.color.colorSecondary)
                findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.ADDED_SUCCESS))
            } else if (it == false){
                findNavController().navigate(NavigationDirections.navigateToMessageDialog(
                    MessageDialog.MessageType.MESSAGE.apply { value.message = getString(R.string.dialog_message_goal_setting_failure) }
                ))
            }
        })

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).back2DiaryFragment()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.buttonBack2Main.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToProfileFragment())
            (activity as MainActivity).back2DiaryFragment()

        }

        binding.profileGoalSettingReference.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToGoalSettingDialog())
        }

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).backFromEditPage()

    }

    override fun onCalendarNextPressed() {

        binding.smartCustomCalendar.getAndSetDataShape()
        binding.smartCustomCalendar.recordedDate.observe(this, Observer {
            binding.smartCustomCalendar.updateCalendar()
        })

    }

    override fun onCalendarPreviousPressed() {

        binding.smartCustomCalendar.getAndSetDataShape()
        binding.smartCustomCalendar.recordedDate.observe(this, Observer {
            binding.smartCustomCalendar.updateCalendar()
        })

    }
}