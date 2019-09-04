package com.terricom.mytype.diary

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.data.UserManager

class DiaryViewModel: ViewModel() {

    val userUid = UserManager.uid



    val db = FirebaseFirestore.getInstance()
    val users: CollectionReference = db.collection("Users")

    val sleep = db.collection("Users")

    val oneDiary = users
        .document(userUid as String).collection("Diary")


    fun getDiary() {
        oneDiary.get()
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