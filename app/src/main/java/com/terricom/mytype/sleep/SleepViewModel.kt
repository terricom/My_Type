package com.terricom.mytype.sleep

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp

class SleepViewModel: ViewModel() {

    val wakeUp = MutableLiveData<Timestamp>()
    val goToSleep = MutableLiveData<Timestamp>()
    val sleepHr = MutableLiveData<Int>()

    val db = FirebaseFirestore.getInstance()
    val sleep = db.collection("Users")

    fun setWakeTime(time: Timestamp){
        wakeUp.value = time
    }

    fun setSleepTime(time: Timestamp){
        goToSleep.value = time
    }

    fun setSleepHr(hours: Long, minitues: Long): Int{
        sleepHr.value = hours.toInt()
        return hours.plus(minitues/60).toInt()
    }

    fun addSleepHr(){

        //發文功能
        val sleepContent = hashMapOf(
            "wakeUp" to wakeUp.value,
            "goToBed" to goToSleep.value,
            "sleepHr" to sleepHr.value
        )

        sleep.get()
            .addOnSuccessListener { result->
                for (doc in result){
                    if (doc["user_name"]== "Terri 醬"){
                        sleep.document(doc.id).collection("Sleep").document().set(sleepContent)
                    }
                }

            }


    }
}