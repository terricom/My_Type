package com.terricom.mytype.linechart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.Logger
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.UserManager
import java.text.SimpleDateFormat
import java.util.*


class CalendarLinechartViewModel: ViewModel() {

    val userUid = UserManager.uid

    val sdf = SimpleDateFormat("yyyy-MM")
    val currentDate = sdf.format(Date())

    private val _date = MutableLiveData<String>()

    val date: LiveData<String>
        get() = _date

    fun setDate(date: String){
        _date.value = date
    }

    val _fireFoodie = MutableLiveData<List<Foodie>>()
    val fireFoodie : LiveData<List<Foodie>>
        get() = _fireFoodie

    fun fireFoodieBack (foo: List<Foodie>){
        _fireFoodie.value = foo
    }


    val db = FirebaseFirestore.getInstance()
    val users: CollectionReference = db.collection("Users")

    val foodieDiary = users
        .document(userUid as String).collection("Foodie")
        .orderBy("timestamp", Query.Direction.DESCENDING)

//    init {
//        setDate(currentDate)
//        getThisMonth()
//    }

    fun getThisMonth() {
        foodieDiary
            .get()
            .addOnSuccessListener {
                val items = mutableListOf<Foodie>()
                for (document in it) {
                    val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                    if (sdf.format(convertDate) == date.value){
                        items.add(document.toObject(Foodie::class.java))
                    }
                }
                if (items.size != 0) {
                    Logger.i("CalendarLinechartViewModel items fireFoodieBack = $items")
                }
                fireFoodieBack(items)
            }
    }

    }