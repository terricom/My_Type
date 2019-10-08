package com.terricom.mytype.sleep

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
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.tools.isConnected
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class SleepFragment: Fragment() {

    private lateinit var binding: FragmentSleepRecordBinding
    private val viewModel: SleepViewModel by lazy{
        ViewModelProviders.of(this).get(SleepViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //        AndroidThreeTen.init(this)


        binding = FragmentSleepRecordBinding.inflate(inflater)
        binding.viewModel = viewModel
        val sleep = SleepFragmentArgs.fromBundle(arguments!!).selectedProperty
        if (sleep.timestamp != null){
//            if (sleep.wakeUp!= null){
//                viewModel.setWakeTime(Timestamp(sleep.wakeUp.time))
//            }
//            if (sleep.goToBed!= null){
//                viewModel.setSleepTime(Timestamp(sleep.goToBed.time))
//            }
//            if (sleep.sleepHr!= null){
//                viewModel.setSleepHr(sleep.goToBed!!, sleep.wakeUp!!)
//            }
            binding.timePicker.setTime(org.threeten.bp.LocalTime.of(SimpleDateFormat("HH").format(sleep.goToBed).toInt()
                , SimpleDateFormat("mm").format(sleep.goToBed).toInt())
                , org.threeten.bp.LocalTime.of(SimpleDateFormat("HH").format(sleep.wakeUp).toInt()
                    , SimpleDateFormat("mm").format(sleep.wakeUp).toInt()))
            binding.timePicker.listener = { bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime ->
                Timber.d("time changed \nbedtime= $bedTime\nwaketime=$wakeTime")
                handleUpdate(bedTime, wakeTime)
            }
            handleUpdate(binding.timePicker.getBedTime(), binding.timePicker.getWakeTime())

            binding.shapeRecordTitle.setText("修改睡眠")
            viewModel.sleepDocId.value = sleep.docId

            binding.buttonSleepSave.setOnClickListener {
                viewModel.updateSleepHr()
            }

        } else {
            binding.timePicker.setTime(org.threeten.bp.LocalTime.of(23, 0), org.threeten.bp.LocalTime.of(7, 0))

            binding.timePicker.listener = { bedTime: org.threeten.bp.LocalTime, wakeTime: org.threeten.bp.LocalTime ->
                Timber.d("time changed \nbedtime= $bedTime\nwaketime=$wakeTime")
                handleUpdate(bedTime, wakeTime)
            }
            handleUpdate(binding.timePicker.getBedTime(), binding.timePicker.getWakeTime())

            binding.buttonSleepSave.setOnClickListener {
                if (isConnected()) {
                    Logger.i("NetworkConnection Network Connected.")
                    //執行下載任務
                    viewModel.addSleepHr()
                }else{
                    Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
                    //告訴使用者網路無法使用
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
//        viewModel.setSleepHr(hours,minutes)
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
                viewModel.setSleepHr(java.sql.Date(Timestamp.valueOf(newBedDate).time), java.sql.Date(Timestamp.valueOf(newWakeDate).time))
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
                viewModel.setSleepHr(java.sql.Date(Timestamp.valueOf(newBedDate).time), java.sql.Date(Timestamp.valueOf(newWakeDate).time))
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
            viewModel.setSleepHr(java.sql.Date(Timestamp.valueOf(newBedDate).time), java.sql.Date(Timestamp.valueOf(newWakeDate).time))
            Logger.i("newBedDate = $newBedDate newWakeDate =$newWakeDate")
        }

    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).backFromEditPage()

    }




}