package com.terricom.mytype.goalsetting

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.*
import com.terricom.mytype.databinding.FragmentGoalSettingBinding
import com.terricom.mytype.shaperecord.ShapeCalendarFragment
import com.terricom.mytype.tools.Logger
import kotlinx.android.synthetic.main.activity_main.*

class GoalSettingFragment: Fragment(), ShapeCalendarFragment.EventBetweenCalendarAndFragment {

    private val viewModel: GoalSettingViewModel by lazy {
        ViewModelProviders.of(this).get(GoalSettingViewModel::class.java)
    }

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
            viewModel.updateGoal(goal)
            binding.shapeRecordTitle.text = App.applicationContext().getString(R.string.goal_title_adjust_goal)

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
            viewModel.goalDocId.value = goal.docId

            binding.smartCustomCalendar.setEventHandler(this)
            binding.smartCustomCalendar.filterdate(goal.deadline)
            binding.smartCustomCalendar.getThisMonth()
            binding.smartCustomCalendar.selectedDayOut = goal.deadline

            binding.smartCustomCalendar.isSelected = true
            binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
                binding.smartCustomCalendar.updateCalendar()
            })
            binding.textGoalSave.text = App.applicationContext().getString(R.string.add_new_confirm)
            binding.buttonGoalSettingSave.setOnClickListener {
                it.background = App.applicationContext().getDrawable(R.color.colorSecondary)
                viewModel.adjustGoal()
            }

        }else {

            binding.smartCustomCalendar.setEventHandler(this)
            binding.smartCustomCalendar.filterdate(binding.smartCustomCalendar.selectedDayOut)
            binding.smartCustomCalendar.getThisMonth()
            binding.smartCustomCalendar.recordedDate.observe(this, Observer {
                binding.smartCustomCalendar.updateCalendar()
            })


            binding.buttonGoalSettingSave.setOnClickListener{

                binding.smartCustomCalendar.selectDateOut?.let {
                    Logger.i("binding.smartCustomCalendar.selectDateOut = $it")
                    viewModel.setDate(it)
                }
                if (isConnected()) {
                    if ((viewModel.water.value ?: 0f).plus(viewModel.fruit.value ?: 0f).plus(viewModel.vegetable.value ?: 0f)
                            .plus(viewModel.oil.value ?: 0f).plus(viewModel.protein.value ?: 0f).plus(viewModel.carbon.value ?: 0f)
                            .plus(viewModel.weight.value ?: 0f).plus(viewModel.muscle.value ?: 0f).plus(viewModel.bodyFat.value ?: 0f) != 0f){
                    viewModel.addGoal()
                    }
                    else {
                        Toast.makeText(App.applicationContext(), App.applicationContext().getText(R.string.goalsetting_input_hint), Toast.LENGTH_SHORT).show()
                    }
                    Logger.i("NetworkConnection Network Connected.")
                    //執行下載任務
                }else{
                    Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
                    //告訴使用者網路無法使用
                }
            }
        }



        viewModel.addGoal.observe(this, Observer {
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
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
                (activity as MainActivity).fab.visibility = View.VISIBLE
                (activity as MainActivity).closeFABMenu()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.buttonBack2Main.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToProfileFragment())
            (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
            (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigate_to_profile_fragment
            (activity as MainActivity).fab.visibility = View.VISIBLE
            (activity as MainActivity).closeFABMenu()

        }

        binding.profileGoalSettingReference.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToGoalSettingDialog())
        }



        return binding.root
    }

    private fun isConnected(): Boolean{
        val connectivityManager = App.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout3.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout4.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false

    }

    override fun onCalendarNextPressed() {
        binding.smartCustomCalendar.filterdate(binding.smartCustomCalendar.selectedDayOut)
        binding.smartCustomCalendar.getThisMonth()
        binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.updateCalendar()
        })

    }

    override fun onCalendarPreviousPressed() {
        binding.smartCustomCalendar.filterdate(binding.smartCustomCalendar.selectedDayOut)
        binding.smartCustomCalendar.getThisMonth()
        binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.updateCalendar()
        })

    }
}