package com.terricom.mytype.sleep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.*
import com.terricom.mytype.databinding.FragmentSleepRecordBinding
import com.terricom.mytype.tools.FORMAT_HH_MM
import com.terricom.mytype.tools.getVmFactory
import com.terricom.mytype.tools.isConnected
import com.terricom.mytype.tools.toDateFormat
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.sql.Timestamp
import java.util.*


class SleepFragment: Fragment() {

    private lateinit var binding: FragmentSleepRecordBinding
    private val viewModel by viewModels<SleepViewModel> { getVmFactory() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSleepRecordBinding.inflate(inflater)
        binding.viewModel = viewModel

        val sleep = SleepFragmentArgs.fromBundle(arguments!!).selectedProperty

        when (sleep.timestamp){

            null -> {
                binding.timePicker.setTime(org.threeten.bp.LocalTime.of(23, 0), org.threeten.bp.LocalTime.of(7, 0))

                binding.timePicker.listener = {
                        bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime ->
                    Timber.d("time changed \nbedtime= $bedTime\nwaketime=$wakeTime")
                    handleUpdate(bedTime, wakeTime)
                }
                handleUpdate(binding.timePicker.getBedTime(), binding.timePicker.getWakeTime())

                binding.buttonSleepSave.setOnClickListener {
                    if (isConnected()) {
                        //執行下載任務
                        viewModel.addOrUpdateSleepHr("")
                    }else{
                        Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
                        //告訴使用者網路無法使用
                    }
                }
            }

            else -> {

                binding.timePicker.setTime(
                    org.threeten.bp.LocalTime.of(
                        sleep.goToBed.toDateFormat(FORMAT_HH_MM).split(":")[0].toInt()
                    , sleep.goToBed.toDateFormat(FORMAT_HH_MM).split(":")[1].toInt())
                    , org.threeten.bp.LocalTime.of(
                        sleep.wakeUp.toDateFormat(FORMAT_HH_MM).split(":")[0].toInt()
                        , sleep.wakeUp.toDateFormat(FORMAT_HH_MM).split(":")[1].toInt()
                    )
                )
                binding.timePicker.listener = { bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime ->
                    Timber.d("time changed \nbedtime= $bedTime\nwaketime=$wakeTime")
                    handleUpdate(bedTime, wakeTime)
                }
                handleUpdate(binding.timePicker.getBedTime(), binding.timePicker.getWakeTime())

                binding.shapeRecordTitle.text = App.applicationContext().getString(R.string.sleep_adjust_title)

                binding.buttonSleepSave.setOnClickListener {
                    viewModel.addOrUpdateSleepHr(sleep.docId)
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).back2DiaryFragment()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.buttonBack2Main.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
            (activity as MainActivity).back2DiaryFragment()

        }


        viewModel.addSleepResult.observe(this, androidx.lifecycle.Observer {
            if (it == true){
                this.findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.ADDED_SUCCESS))
            } else if (it == false){
                findNavController().navigate(NavigationDirections.navigateToMessageDialog(
                    MessageDialog.MessageType.MESSAGE.apply { value.message = getString(R.string.dialog_message_sleep_record_failure) }
                ))
                binding.buttonSleepSave.background  = App.applicationContext().getDrawable(R.color.colorMyType)
            }
        })



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

        if (minutes > 0) binding.llMins.visibility = View.VISIBLE else binding.llMins.visibility = View.GONE

        if (bedDate < wakeDate){

            if (bedDate.dayOfMonth == wakeDate.dayOfMonth){

                val newBedDate = "${bedDate.year}" +
                        "-${bedDate.monthValue}" +
                        "-${bedDate.dayOfMonth} ${bedDate.hour}" +
                        ":${bedDate.minute}:${bedDate.second}.000000000"
                var timeStampBed = Timestamp.valueOf(newBedDate)
                viewModel.setSleepTime(timeStampBed)
                val newWakeDate = "${wakeDate.year}" +
                        "-${wakeDate.monthValue}" +
                        "-${wakeDate.dayOfMonth} ${wakeDate.hour}" +
                        ":${wakeDate.minute}:${wakeDate.second}.000000000"
                var timeStampWake = Timestamp.valueOf(newWakeDate)
                viewModel.setWakeTime(timeStampWake)
                viewModel.setSleepHr(java.sql.Date(Timestamp.valueOf(newBedDate).time),
                    java.sql.Date(Timestamp.valueOf(newWakeDate).time))

            }else if (bedDate.dayOfMonth < wakeDate.dayOfMonth){

                wakeDate.minusDays(1)
                bedDate.minusDays(1)
                val newBedDate = "${bedDate.minusDays(1).year}" +
                        "-${bedDate.minusDays(1).monthValue}" +
                        "-${bedDate.minusDays(1).dayOfMonth} ${bedDate.minusDays(1).hour}" +
                        ":${bedDate.minute}:${bedDate.minusDays(1).second}.000000000"
                var timeStampBed = Timestamp.valueOf(newBedDate)
                viewModel.setSleepTime(timeStampBed)
                val newWakeDate = "${wakeDate.minusDays(1).year}" +
                        "-${wakeDate.minusDays(1).monthValue}" +
                        "-${wakeDate.minusDays(1).dayOfMonth} ${wakeDate.minusDays(1).hour}" +
                        ":${wakeDate.minusDays(1).minute}:${wakeDate.minusDays(1).second}.000000000"
                var timeStampWake = Timestamp.valueOf(newWakeDate)
                viewModel.setWakeTime(timeStampWake)
                viewModel.setSleepHr(java.sql.Date(Timestamp.valueOf(newBedDate).time), java.sql.Date(Timestamp.valueOf(newWakeDate).time))
            }

        }else if (bedDate == wakeDate){

            wakeDate.plusDays(1)
            bedDate.plusDays(1)
            val newBedDate = "${bedDate.year}" +
                    "-${bedDate.monthValue}" +
                    "-${bedDate.plusDays(1).dayOfMonth} ${bedDate.hour}" +
                    ":${bedDate.minute}:${bedDate.second}.000000000"
            var timeStampBed = Timestamp.valueOf(newBedDate)
            viewModel.setSleepTime(timeStampBed)
            val newWakeDate = "${wakeDate.year}" +
                    "-${wakeDate.monthValue}" +
                    "-${wakeDate.plusDays(1).dayOfMonth} ${wakeDate.hour}" +
                    ":${wakeDate.minute}:${wakeDate.second}.000000000"
            var timeStampWake = Timestamp.valueOf(newWakeDate)
            viewModel.setWakeTime(timeStampWake)
            viewModel.setSleepHr(java.sql.Date(Timestamp.valueOf(newBedDate).time), java.sql.Date(Timestamp.valueOf(newWakeDate).time))
        }

    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).backFromEditPage()

    }




}