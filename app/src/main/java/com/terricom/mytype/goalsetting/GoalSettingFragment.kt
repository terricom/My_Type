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
import com.terricom.mytype.tools.toDemicalPoint
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
            viewModel.water.value = goal.water.toDemicalPoint(1)
            viewModel.fruit.value = goal.fruit.toDemicalPoint(1)
            viewModel.protein.value = goal.protein.toDemicalPoint(1)
            viewModel.vegetable.value = goal.vegetable.toDemicalPoint(1)
            viewModel.oil.value = goal.oil.toDemicalPoint(1)
            viewModel.carbon.value = goal.carbon.toDemicalPoint(1)
            viewModel.weight.value = goal.weight.toDemicalPoint(1)
            viewModel.bodyFat.value = goal.bodyFat.toDemicalPoint(1)
            viewModel.muscle.value = goal.muscle.toDemicalPoint(1)
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

                    if (((viewModel.water.value ?: "0.0").toFloat()).plus((viewModel.fruit.value ?: "0.0").toFloat())
                            .plus((viewModel.vegetable.value ?: "0.0").toFloat())
                            .plus((viewModel.oil.value ?: "0.0").toFloat())
                            .plus((viewModel.protein.value ?: "0.0").toFloat())
                            .plus((viewModel.carbon.value ?: "0.0").toFloat())
                            .plus((viewModel.weight.value ?: "0.0").toFloat())
                            .plus((viewModel.muscle.value ?: "0.0").toFloat())
                            .plus((viewModel.bodyFat.value ?: "0.0").toFloat()) == 0f){

                        Toast.makeText(App.applicationContext(), App.applicationContext().getText(R.string.goalsetting_input_hint), Toast.LENGTH_SHORT).show()
                    }

                    else if ((viewModel.water.value ?: "0.0").toFloat()>10 ||
                        (viewModel.oil.value ?: "0.0").toFloat()>10 ||
                        (viewModel.vegetable.value ?: "0.0").toFloat()>10 ||
                        (viewModel.protein.value ?: "0.0").toFloat()>10 ||
                        (viewModel.carbon.value ?: "0.0").toFloat()>10 ||
                        (viewModel.fruit.value ?: "0.0").toFloat()>10){

                        Toast.makeText(App.applicationContext(), App.applicationContext().getText(R.string.goal_hint_over_eat), Toast.LENGTH_SHORT).show()

                    }else if ((viewModel.weight.value ?: "0.0").toFloat()>200 ||
                        (viewModel.muscle.value ?: "0.0").toFloat()>100 ||
                        (viewModel.bodyFat.value ?: "0.0").toFloat()>100) {

                        Toast.makeText(
                            App.applicationContext(),
                            App.applicationContext().getText(R.string.goal_setting_hint_over_shape),
                            Toast.LENGTH_SHORT
                        ).show()

                    }else {
                        viewModel.addGoal(goal.docId)
                    }
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

                    if (((viewModel.water.value ?: "0.0").toFloat()).plus((viewModel.fruit.value ?: "0.0").toFloat())
                            .plus((viewModel.vegetable.value ?: "0.0").toFloat())
                            .plus((viewModel.oil.value ?: "0.0").toFloat())
                            .plus((viewModel.protein.value ?: "0.0").toFloat())
                            .plus((viewModel.carbon.value ?: "0.0").toFloat())
                            .plus((viewModel.weight.value ?: "0.0").toFloat())
                            .plus((viewModel.muscle.value ?: "0.0").toFloat())
                            .plus((viewModel.bodyFat.value ?: "0.0").toFloat()) == 0f){

                        Toast.makeText(App.applicationContext(), App.applicationContext().getText(R.string.goalsetting_input_hint), Toast.LENGTH_SHORT).show()
                    }

                    else if ((viewModel.water.value ?: "0.0").toFloat()>10 ||
                        (viewModel.oil.value ?: "0.0").toFloat()>10 ||
                        (viewModel.vegetable.value ?: "0.0").toFloat()>10 ||
                        (viewModel.protein.value ?: "0.0").toFloat()>10 ||
                        (viewModel.carbon.value ?: "0.0").toFloat()>10 ||
                        (viewModel.fruit.value ?: "0.0").toFloat()>10){

                        Toast.makeText(App.applicationContext(), App.applicationContext().getText(R.string.goal_hint_over_eat), Toast.LENGTH_SHORT).show()

                    }else if ((viewModel.weight.value ?: "0.0").toFloat()>200 ||
                        (viewModel.muscle.value ?: "0.0").toFloat()>100 ||
                        (viewModel.bodyFat.value ?: "0.0").toFloat()>100) {

                        Toast.makeText(
                            App.applicationContext(),
                            App.applicationContext().getText(R.string.goal_setting_hint_over_shape),
                            Toast.LENGTH_SHORT
                        ).show()

                    }else {
                        viewModel.addGoal("")
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