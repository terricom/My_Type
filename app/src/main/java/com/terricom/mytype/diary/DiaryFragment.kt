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
import com.terricom.mytype.App
import com.terricom.mytype.Logger
import com.terricom.mytype.R
import com.terricom.mytype.calendar.CalendarFragment
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.databinding.FragmentDiaryBinding
import java.text.SimpleDateFormat
import java.util.*


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
                binding.backgroundOfIcon.visibility = View.GONE
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
                    viewModel.filterdate(binding.diaryCalendar.selectedDayOut ?:Date())
                    if (viewModel.fireFoodie.value!!.size <= 0){
                        binding.diaryHintAddFoodie.visibility = View.VISIBLE
                        binding.iconMyType.visibility = View.VISIBLE
                        binding.backgroundOfIcon.visibility = View.VISIBLE
                    }
                    viewModel.calendarClickedAgain()
                }
            }else if (it == false){
                binding.buttonSaveCalendar.setOnClickListener {
                    binding.buttonExpandArrow.animate().rotation(0f).start()
                    binding.diaryCalendar.visibility = View.VISIBLE
                    binding.diaryHintAddFoodie.visibility = View.INVISIBLE
                    binding.iconMyType.visibility = View.INVISIBLE
                    binding.backgroundOfIcon.visibility = View.INVISIBLE
                    viewModel.calendarClicked()
                }

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
            Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT)
            //告訴使用者網路無法使用
        }

        viewModel.filterdate(binding.diaryCalendar.selectedDayOut ?: Date())
        Logger.i("binding.diaryCalendar.selectedDayOut = ${binding.diaryCalendar.selectedDayOut}")

        viewModel.date.observe(this, Observer {
            Logger.i("viewModel.date.observe === $it")
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            viewModel.getDiary()
            viewModel.getThisMonth()
            binding.diaryDate.text = sdf.format(it)
        })


        return binding.root
    }

    override fun onCalendarNextPressed() {
        binding.diaryCalendar.updateCalendar()
    }

    override fun onCalendarPreviousPressed() {
        binding.diaryCalendar.updateCalendar()
    }


}