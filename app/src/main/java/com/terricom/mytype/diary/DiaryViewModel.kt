package com.terricom.mytype.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.Logger
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.Sleep
import com.terricom.mytype.data.UserManager
import java.text.SimpleDateFormat
import java.util.*

class DiaryViewModel: ViewModel() {

    val userUid = UserManager.uid
    val _date = MutableLiveData<String>()
    val date : LiveData<String>
        get() = _date

    val _fireFoodie = MutableLiveData<List<Foodie>>()
    val fireFoodie : LiveData<List<Foodie>>
        get() = _fireFoodie

    fun fireFoodieBack (foo: List<Foodie>){
        _fireFoodie.value = foo
    }

    val _fireShape = MutableLiveData<Shape>()
    val fireShape : LiveData<Shape>
        get() = _fireShape

    fun fireShapeBack (shape: Shape){
        _fireShape.value = shape
    }

    val _fireSleep = MutableLiveData<Sleep>()
    val fireSleep : LiveData<Sleep>
        get() = _fireSleep

    fun fireSleepBack (sleep: Sleep){
        _fireSleep.value = sleep
    }

    fun filterdate(dato: String){
        _date.value = dato
    }

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val currentDate = sdf.format(Date())

    val db = FirebaseFirestore.getInstance()
    val users: CollectionReference = db.collection("Users")

    val foodieDiary = users
        .document(userUid as String).collection("Foodie")
        .orderBy("timestamp", Query.Direction.DESCENDING)
    val shapeDiary = users
        .document(userUid as String).collection("Shape")
        .orderBy("timestamp", Query.Direction.DESCENDING)
    val sleepDiary = users
        .document(userUid as String).collection("Sleep")
        .orderBy("timestamp", Query.Direction.DESCENDING)



    init {
        Logger.i("currentDate =$currentDate")
        filterdate(currentDate)
        getDiary()
    }

    fun getDiary() {

        shapeDiary
            .get()
            .addOnSuccessListener {
                val items = mutableListOf<Shape>()
                for (document in it) {
                    val convertDate = java.sql.Date(document.toObject(Shape::class.java).timestamp!!.time)
                    if (convertDate.toString() == date.value){
                        items.add(document.toObject(Shape::class.java))
                    }
                }
                Logger.i("DiaryViewModel items fireShapeBack (items[0]) = ${items[0]}")
                fireShapeBack(items[0])
            }

        sleepDiary
            .get()
            .addOnSuccessListener {
                val items = mutableListOf<Sleep>()
                for (document in it) {
                    val convertDate = java.sql.Date(document.toObject(Sleep::class.java).wakeUp!!.time)
                    if (convertDate.toString() == date.value){
                        items.add(document.toObject(Sleep::class.java))
                    }

                }
                Logger.i("DiaryViewModel items fireSleepBack (items[0]) = ${items[0]}")

                fireSleepBack(items[0])
            }

        foodieDiary
            .get()
            .addOnSuccessListener {
                val items = mutableListOf<Foodie>()
                for (document in it) {
                    val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                    if (convertDate.toString() == date.value){
                        items.add(document.toObject(Foodie::class.java))
                    }

                }
                Logger.i("DiaryViewModel items fireFoodieBack = $items")

                fireFoodieBack(items)
            }



    }





}