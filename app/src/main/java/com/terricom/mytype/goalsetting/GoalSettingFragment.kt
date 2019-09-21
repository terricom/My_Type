package com.terricom.mytype.goalsetting

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
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

        binding = FragmentGoalSettingBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.buttonGoalSettingSave.setOnClickListener{
            binding.smartCustomCalendar.selectDateOut?.let {
                Logger.i("binding.smartCustomCalendar.selectDateOut = $it")
                viewModel.setDate(it)
            }
            if (isConnected()) {
                viewModel.addGoal()
                Logger.i("NetworkConnection Network Connected.")
                //執行下載任務
            }else{
                Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT)
                //告訴使用者網路無法使用
            }
        }

        viewModel.addGoal.observe(this, Observer {
            if (it == true){
                findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.ADDED_SUCCESS))
                Handler().postDelayed({
                    findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                    (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                    (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
                    (activity as MainActivity).fab.visibility = View.VISIBLE
                    (activity as MainActivity).closeFABMenu()
                },2005)
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
            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
            (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
            (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
            (activity as MainActivity).fab.visibility = View.VISIBLE
            (activity as MainActivity).closeFABMenu()

        }

        binding.profileGoalSettingReference.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToGoalSettingDialog())
        }

        binding.smartCustomCalendar.setEventHandler(this)
        binding.smartCustomCalendar.filterdate(binding.smartCustomCalendar.selectedDayOut)
        binding.smartCustomCalendar.getThisMonth()
        binding.smartCustomCalendar.recordedDate.observe(this, Observer {
            binding.smartCustomCalendar.updateCalendar()
        })


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