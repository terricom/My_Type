package com.terricom.mytype.diary

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.App
import com.terricom.mytype.MessageDialog
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import com.terricom.mytype.calendar.CalendarComponentLayout
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.databinding.FragmentDiaryBinding
import com.terricom.mytype.tools.Logger
import java.text.SimpleDateFormat


class DiaryFragment: Fragment(), CalendarComponentLayout.EventBetweenCalendarAndFragment
{

    private val viewModel: DiaryViewModel by lazy {
        ViewModelProviders.of(this).get(DiaryViewModel::class.java)
    }
    private lateinit var binding :FragmentDiaryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDiaryBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        Logger.i("findNavController().navigate(NavigationDirections.navigateToDiaryFragment()) in DIARY")
        binding.recyclerView.adapter = FoodieAdapter(viewModel, FoodieAdapter.OnClickListener{foodie ->
                findNavController().navigate(NavigationDirections.navigateToFoodieFragment(foodie))
        })

        viewModel.getPuzzle.observe(this, Observer {
            Logger.i("viewModel.getPuzzle.observe= $it UserManager.createDiary = ${UserManager.createDiary}")
            if (it == false && UserManager.createDiary == "2"){
                this.findNavController().navigate(NavigationDirections.navigateToMessageDialog(
                    MessageDialog.MessageType.GET_PUZZLE.apply {
                    value.message = App.applicationContext().resources.getString(R.string.diary_puzzle_check_new)
                }))
            }else if (it == true && UserManager.getPuzzleOld == "1"){
                this.findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.GET_PUZZLE.apply {
                    value.message = App.applicationContext().resources.getString(R.string.diary_puzzle_check_old)
                }))
                UserManager.getPuzzleOld = UserManager.getPuzzleOld.toString().toInt().plus(1).toString()
            }
        })

        viewModel.callDeleteAction.observe(this, Observer {
            if (it==true){
                viewModel.getDiary()
            }
        })

        viewModel.fireFoodie.observe(this, Observer {
            if (it != null){
                (binding.recyclerView.adapter as FoodieAdapter).addHeaderAndSubmitList(it)
                (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
            }
        })

        viewModel.date.observe(this, Observer {
            Logger.i("viewModel.date.observe === $it")
            val sdf = SimpleDateFormat(App.applicationContext().getString(R.string.simpledateformat_yyyy_MM_dd))
            if (it != null){
                viewModel.getDiary()
                viewModel.getThisMonth()
                binding.diaryDate.text = sdf.format(it)

                binding.diaryCalendar.setEventHandler(this)
                binding.diaryCalendar.filterdate(binding.diaryCalendar.selectedDayOut)
                binding.diaryCalendar.getThisMonth()
                binding.diaryCalendar.recordedDate.observe(this, Observer {
                    if (!it.isNullOrEmpty()){
                        binding.diaryCalendar.updateCalendar()
                    }
                })


                viewModel.fireSleep.observe(this, Observer {
                    Logger.i("viewModel.fireSleep.observe = $it")
                    if (it != null){

                        (binding.recyclerView.adapter as FoodieAdapter).addHeaderAndSubmitList(viewModel.fireFoodie.value)
                        (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
                    }

                })
                viewModel.fireShape.observe(this, Observer {
                    Logger.i("viewModel.fireShape.observe =$it")
                    if (it != null){
                        (binding.recyclerView.adapter as FoodieAdapter).addHeaderAndSubmitList(viewModel.fireFoodie.value)
                        (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
                    }

                })
                viewModel.fireFoodie.observe(this, Observer {
                    (binding.recyclerView.adapter as FoodieAdapter).addHeaderAndSubmitList(it)
                    (binding.recyclerView.adapter as FoodieAdapter).notifyDataSetChanged()
                })
            }
        })

        binding.recyclerView.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen.elevation_all).toInt(),
                true
            )
        )

        viewModel.calendarClicked.observe(this, Observer {

            if (it == true){

                binding.diaryDate.setOnClickListener {
                    binding.buttonExpandArrow.animate().rotation(0f).start()
                    binding.diaryCalendar.animate().translationY(-resources.getDimension(R.dimen.standard_305)).start()
                    binding.diaryCalendar.visibility = View.GONE
                    viewModel.filterdate(binding.diaryCalendar.selectedDayOut)
                    viewModel.calendarClickedAgain()
                }

            }else if (it == false){

                binding.diaryDate.setOnClickListener {
                    binding.buttonExpandArrow.animate().rotation(180f).start()
                    binding.diaryCalendar.animate().translationY(resources.getDimension(R.dimen.standard_0)).start()
                    binding.diaryCalendar.visibility = View.VISIBLE
                    binding.diaryCalendar.getThisMonth()
                    viewModel.calendarClicked()
                }
            }
        })

        viewModel.listFoodie.observe(this, Observer {
            if (it != null){

                findNavController().navigate(NavigationDirections.navigateToQueryFragment(it))
            }
        })

        fun isConnected(): Boolean{
            val connectivityManager = App.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)
            return if (connectivityManager is ConnectivityManager) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                networkInfo?.isConnected ?: false
            } else false
        }

        if (isConnected()) {
            Logger.i("NetworkConnection Network Connected.")
            //執行下載任務
        }else{
            Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
            //告訴使用者網路無法使用
        }

        viewModel.filterdate(binding.diaryCalendar.selectedDayOut)
        Logger.i("binding.diaryCalendar.selectedDayOut = ${binding.diaryCalendar.selectedDayOut}")

        return binding.root
    }

    override fun onCalendarNextPressed() {


        binding.diaryCalendar.filterdate(binding.diaryCalendar.selectedDayOut)
        binding.diaryCalendar.getThisMonth()
        binding.diaryCalendar.recordedDate.observe(this, Observer {
            binding.diaryCalendar.updateCalendar()
        })
    }

    override fun onCalendarPreviousPressed() {

        binding.diaryCalendar.filterdate(binding.diaryCalendar.selectedDayOut)
        binding.diaryCalendar.getThisMonth()
        binding.diaryCalendar.recordedDate.observe(this, Observer {
            binding.diaryCalendar.updateCalendar()
        })
    }


}