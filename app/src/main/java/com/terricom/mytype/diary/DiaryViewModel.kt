package com.terricom.mytype.diary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class DiaryViewModel: ViewModel() {

    private val dropItems = mutableListOf<String>()

    val addNutrition = MutableLiveData<String>()
    val adddNutrition: LiveData<String>
        get() = addNutrition

    fun dragToList(nutrition: String) {
        dropItems.add(nutrition)
        Log.i("Terri", "DiaryViewModel dropItems = $dropItems")
    }

    val db = FirebaseFirestore.getInstance()
    val users: CollectionReference = db.collection("Users")

    val diary = db.collection("Users")

    val oneDiary = db.collection("Users")
        .document().collection("Diary")

    init {
        get()
        faFeiWen()

    }

    fun get() {
        users.get()
            .addOnSuccessListener {
                for (document in it) {
//                            userId = document.id
//                    var userlist : List<User> ?= null
//                    _user.value = document.toObject(User::class.java)
//                    _userList.value = userlist
                    Log.d("Terri", document.id + " => " + document.data)

                }
            }

    }


    fun faFeiWen(){

        //發文功能
        val article1 = hashMapOf(
            "memo" to "臭豆腐好臭RRR",
            "gotobed" to "08:00",
            "wakeup" to "14:00",
            "timestamp" to Timestamp.now(),
            "nutritions" to listOf("葉黃素", "維他命", "葡萄糖")
        )

        diary.get()
            .addOnSuccessListener { result->
            for (doc in result){
                if (doc["user_name"]== "Terri 醬"){
                    diary.document(doc.id).collection("Diary").document().set(article1)
                }
            }

        }


    }



}