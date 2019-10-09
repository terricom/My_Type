package com.terricom.mytype.sleep

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Sleep
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.toDateFormat
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class SleepViewModel: ViewModel() {

    private val wakeUp = MutableLiveData<Timestamp>()
    private val goToSleep = MutableLiveData<Timestamp>()
    private val sleepHr = MutableLiveData<Float>()

    private val _sleepToday = MutableLiveData<List<Sleep>>()
    private val sleepToday : LiveData<List<Sleep>>
        get() = _sleepToday

    private fun setSleepToday(sleepToday: List<Sleep>){
        _sleepToday.value = sleepToday
    }

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

    init {
        checkSleepRecord()
    }

    fun addOrUpdateSleepHr(docId: String){

        val sleepContent = hashMapOf(
            FirebaseKey.COLUMN_SLEEP_WAKE_UP to wakeUp.value,
            FirebaseKey.COLUMN_SLEEP_GO_TO_BED to goToSleep.value,
            FirebaseKey.COLUMN_SLEEP_HR to sleepHr.value,
            FirebaseKey.TIMESTAMP to Timestamp(Date().time)
        )

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {userDocument ->

                when (docId){

                    "" ->{
                        if (sleepToday.value.isNullOrEmpty()){

                            userDocument.collection(FirebaseKey.COLLECTION_SLEEP).document().set(sleepContent)
                            addSleepSuccess()
                        } else {

                            addSleepFail()
                        }
                    }
                    else -> {
                        userDocument.collection(FirebaseKey.COLLECTION_SLEEP).document(docId).set(sleepContent)
                        addSleepSuccess()
                    }

                }
            }
        }
    }


    private fun checkSleepRecord(): Boolean {
        val items = mutableListOf<Sleep>()
        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = Date()
        val localDateStart: LocalDate = LocalDate.parse(Date().toDateFormat(FORMAT_YYYY_MM_DD))
        localDateStart.atTime(LocalTime.MIDNIGHT)
        val localDateEnd : LocalDate = LocalDate.parse(Date().toDateFormat(FORMAT_YYYY_MM_DD))
        localDateEnd.atTime(LocalTime.MAX)

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let { userDocument ->

                userDocument.collection(FirebaseKey.COLLECTION_SLEEP)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
                    .whereGreaterThanOrEqualTo(
                        FirebaseKey.TIMESTAMP,
                        Timestamp.valueOf(App.applicationContext().getString(R.string.timestamp_daybegin, "$localDateStart"))
                    )
                    .get()
                    .addOnSuccessListener {
                        for (document in it) {
                            items.add(document.toObject(Sleep::class.java))
                            items[items.size-1].docId = document.id
                        }
                        setSleepToday(items)
                    }
            }
        }

        return items.size != 0
    }

}