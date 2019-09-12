package com.terricom.mytype.sleep

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
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.*
import com.terricom.mytype.databinding.FragmentSleepRecordBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.util.*


class SleepFragment: Fragment() {

    private lateinit var binding: FragmentSleepRecordBinding
    private val viewModel: SleepViewModel by lazy{
        ViewModelProviders.of(this).get(SleepViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //        AndroidThreeTen.init(this)

        binding = FragmentSleepRecordBinding.inflate(inflater)
        binding.timePicker.setTime(org.threeten.bp.LocalTime.of(23, 0), org.threeten.bp.LocalTime.of(7, 0))
        binding.viewModel = viewModel

        binding.timePicker.listener = { bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime ->
            Timber.d("time changed \nbedtime= $bedTime\nwaketime=$wakeTime")
            handleUpdate(bedTime, wakeTime)
        }
        handleUpdate(binding.timePicker.getBedTime(), binding.timePicker.getWakeTime())

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                (activity as MainActivity).fab.visibility = View.VISIBLE
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.buttonShaperecordSave.setOnClickListener {
            if (isConnected()) {
                Logger.i("NetworkConnection Network Connected.")
                //執行下載任務
            }else{
                Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT)
                //告訴使用者網路無法使用
            }
        }

        return binding.root
    }

    private fun handleUpdate(bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime) {

        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
        binding.tvBedTime.text = bedTime.format(formatter)
        binding.tvWakeTime.text = wakeTime.format(formatter)
        val bedDate = bedTime.atDate(LocalDate.now())
        var wakeDate = wakeTime.atDate(LocalDate.now())

        if (bedDate >= wakeDate) wakeDate = wakeDate.plusDays(1)
        val duration = Duration.between(bedDate, wakeDate)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        binding.tvHours.text = hours.toString()
        binding.tvMins.text = minutes.toString()
        viewModel.setSleepHr(hours,minutes)
        if (minutes > 0) binding.llMins.visibility = View.VISIBLE else binding.llMins.visibility = View.GONE

        if (bedDate < wakeDate){
            Logger.i("bedDate == wakeDate ${bedDate.dayOfMonth} - ${wakeDate.dayOfMonth}")
            if (bedDate.dayOfMonth == wakeDate.dayOfMonth){
                val newBedDate = "${bedDate.year}" +
                        "-${bedDate.monthValue}" +
                        "-${bedDate.dayOfMonth} ${bedDate.hour}" +
                        ":${bedDate.minute}:${bedDate.second}.000000000"
                var timeStampBed = java.sql.Timestamp.valueOf(newBedDate)
                viewModel.setSleepTime(timeStampBed)
                val newWakeDate = "${wakeDate.year}" +
                        "-${wakeDate.monthValue}" +
                        "-${wakeDate.dayOfMonth} ${wakeDate.hour}" +
                        ":${wakeDate.minute}:${wakeDate.second}.000000000"
                var timeStampWake = java.sql.Timestamp.valueOf(newWakeDate)
                viewModel.setWakeTime(timeStampWake)
                Logger.i("bedDate == wakeDate newBedDate = $newBedDate newWakeDate =$newWakeDate")
            }else if (bedDate.dayOfMonth < wakeDate.dayOfMonth){
                wakeDate.minusDays(1)
                bedDate.minusDays(1)
                val newBedDate = "${bedDate.minusDays(1).year}" +
                        "-${bedDate.minusDays(1).monthValue}" +
                        "-${bedDate.minusDays(1).dayOfMonth} ${bedDate.minusDays(1).hour}" +
                        ":${bedDate.minute}:${bedDate.minusDays(1).second}.000000000"
                var timeStampBed = java.sql.Timestamp.valueOf(newBedDate)
                viewModel.setSleepTime(timeStampBed)
                val newWakeDate = "${wakeDate.minusDays(1).year}" +
                        "-${wakeDate.minusDays(1).monthValue}" +
                        "-${wakeDate.minusDays(1).dayOfMonth} ${wakeDate.minusDays(1).hour}" +
                        ":${wakeDate.minusDays(1).minute}:${wakeDate.minusDays(1).second}.000000000"
                var timeStampWake = java.sql.Timestamp.valueOf(newWakeDate)
                viewModel.setWakeTime(timeStampWake)
                Logger.i("newBedDate = $newBedDate newWakeDate =$newWakeDate")
            }

        }else if (bedDate == wakeDate){
            Logger.i("bedDate == wakeDate ${bedDate.dayOfMonth} - ${wakeDate.dayOfMonth}")
            wakeDate.plusDays(1)
            bedDate.plusDays(1)
            val newBedDate = "${bedDate.year}" +
                    "-${bedDate.monthValue}" +
                    "-${bedDate.plusDays(1).dayOfMonth} ${bedDate.hour}" +
                    ":${bedDate.minute}:${bedDate.second}.000000000"
            var timeStampBed = java.sql.Timestamp.valueOf(newBedDate)
            viewModel.setSleepTime(timeStampBed)
            val newWakeDate = "${wakeDate.year}" +
                    "-${wakeDate.monthValue}" +
                    "-${wakeDate.plusDays(1).dayOfMonth} ${wakeDate.hour}" +
                    ":${wakeDate.minute}:${wakeDate.second}.000000000"
            var timeStampWake = java.sql.Timestamp.valueOf(newWakeDate)
            viewModel.setWakeTime(timeStampWake)
            Logger.i("newBedDate = $newBedDate newWakeDate =$newWakeDate")
        }

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




}