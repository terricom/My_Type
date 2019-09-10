package com.terricom.mytype.achivement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.UserManager

class AchievementViewModel: ViewModel() {


    val userUid = UserManager.uid

    val _fireShape = MutableLiveData<List<Shape>>()
    val fireShape : LiveData<List<Shape>>
        get() = _fireShape

    fun fireShapeBack (shape: List<Shape>){
        _fireShape.value = shape
    }

    val db = FirebaseFirestore.getInstance()
    val user = db.collection("Users")

    val _userFoodList = MutableLiveData<List<String>>()
    val userFoodList : LiveData<List<String>>
        get() = _userFoodList

    init {
        getShapeList()
    }

    fun getShapeList(){

        if (userUid != null){
            val shapeDiary = user
                .document(userUid).collection("Shape")
                .orderBy("timestamp", Query.Direction.DESCENDING)

        shapeDiary.get()
            .addOnSuccessListener { result->
                val items = mutableListOf<Shape>()
                for (document in result) {
                        items.add(document.toObject(Shape::class.java))
                }

                if (items.size != 0){
                    fireShapeBack(items)
                }


            }
        }


    }

//    fun getGoalList(){
//
//        user.get()
//            .addOnSuccessListener { result ->
//                for (doc in result){
//                    if (doc.id == userUid){
//                        val user = doc.toObject(UserMT::class.java)
//                        if (user != null){
//                            var firebaseFoodlist: List<String> = doc["foodlist"] as List<String>
//                            getFoodlist(firebaseFoodlist)
//                        }
//                        if (user.nutritionlist != null){
//                            var firebaseNulist: List<String> = doc["nutritionlist"] as List<String>
//                            getNulist(firebaseNulist)
//                        }
//                    }
//                }
//
//            }
//    }

}