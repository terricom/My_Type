package com.terricom.mytype.sleep

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.data.UserManager
import java.sql.Timestamp

class SleepViewModel: ViewModel() {

    val userUid = UserManager.uid

    val wakeUp = MutableLiveData<Timestamp>()
    val goToSleep = MutableLiveData<Timestamp>()
    val sleepHr = MutableLiveData<Float>()

    val db = FirebaseFirestore.getInstance()
    val sleep = db.collection("Users")

    fun setWakeTime(time: Timestamp){
        wakeUp.value = time
    }

    fun setSleepTime(time: Timestamp){
        goToSleep.value = time
    }

    fun setSleepHr(hours: Long, minitues: Long): Float{
        sleepHr.value = hours.plus(minitues/60).toFloat()
        return hours.plus(minitues/60).toFloat()
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
                    if (doc.id == userUid){
                        sleep.document(doc.id).collection("Sleep").document().set(sleepContent)
                    }
                }

            }


    }
}