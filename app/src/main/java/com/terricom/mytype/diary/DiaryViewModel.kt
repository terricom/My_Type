package com.terricom.mytype.diary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    val sleep = db.collection("Users")

    val oneDiary = db.collection("Users")
        .document().collection("Diary")

    init {
        get()
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





}