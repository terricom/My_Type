package com.terricom.mytype.sleep

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Result
import com.terricom.mytype.data.Sleep
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.toDateFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*

class SleepViewModel(private val myTypeRepository: MyTypeRepository): ViewModel() {

    private val wakeUp = MutableLiveData<Timestamp>()
    private val goToSleep = MutableLiveData<Timestamp>()
    private val sleepHr = MutableLiveData<Float>()

    fun setWakeTime(time: Timestamp){
        wakeUp.value = time
    }

    fun setSleepTime(time: Timestamp){
        goToSleep.value = time
    }

    fun setSleepHr(sleep: Date, wakeUp: Date): Float{
        sleepHr.value = wakeUp.time.minus(sleep.time)/(1000 * 60 * 60).toFloat()
        return wakeUp.time.minus(sleep.time)/(1000 * 60 * 60).toFloat()
    }

    private val _addSleepResult = MutableLiveData<Boolean>()
    val addSleepResult : LiveData<Boolean>
        get() = _addSleepResult

    private fun addSleepSuccess(){
        _addSleepResult.value = true
    }

    private fun addSleepFail(){
        _addSleepResult.value = false
    }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun addOrUpdateSleepHr(docId: String){

        val sleepContent = hashMapOf(
            FirebaseKey.COLUMN_SLEEP_WAKE_UP to wakeUp.value,
            FirebaseKey.COLUMN_SLEEP_GO_TO_BED to goToSleep.value,
            FirebaseKey.COLUMN_SLEEP_HR to sleepHr.value,
            FirebaseKey.TIMESTAMP to Timestamp(Date().time)
        )

        coroutineScope.launch {

            val sleepResult = myTypeRepository.getObjects<Sleep>(
                FirebaseKey.COLLECTION_SLEEP,
                Timestamp.valueOf(
                    App.applicationContext().getString(
                        R.string.timestamp_daybegin,
                        Date().toDateFormat(FORMAT_YYYY_MM_DD))
                ),
                Timestamp.valueOf(
                    App.applicationContext().getString(
                        R.string.timestamp_dayend,
                        Date().toDateFormat(FORMAT_YYYY_MM_DD))
                ))

            if (sleepResult is Result.Success) {
                when(docId){
                    "" -> {
                        when (sleepResult.data.isEmpty()){
                            true -> {
                                myTypeRepository.setOrUpdateObjects(FirebaseKey.COLLECTION_SLEEP, sleepContent, docId)
                                addSleepSuccess()
                            }
                            false -> addSleepFail()
                        }
                    }
                    else -> {
                        myTypeRepository.setOrUpdateObjects(FirebaseKey.COLLECTION_SLEEP, sleepContent, docId)
                        addSleepSuccess()
                    }
                }
            }
        }
    }
}