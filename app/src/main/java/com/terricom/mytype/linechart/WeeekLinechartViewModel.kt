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

class WeeekLinechartViewModel: ViewModel() {

    val userUid = UserManager.uid

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfM = SimpleDateFormat("M/d")
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

    val _fireDate = MutableLiveData<List<String>>()
    val fireDate : LiveData<List<String>>
        get() = _fireDate

    fun fireDateBack (foo: List<String>){
        _fireDate.value = foo
    }


    val db = FirebaseFirestore.getInstance()
    val users: CollectionReference = db.collection("Users")

    init {
        setDate(currentDate)
        getThisMonth()
    }

    fun getThisMonth() {

        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid as String).collection("Foodie")
                .orderBy("timestamp", Query.Direction.ASCENDING)
            foodieDiary
                .get()
                .addOnSuccessListener {
                    val items = mutableListOf<Foodie>()
                    val datelist = mutableListOf<String>()
                    val waterList = mutableListOf<Float>()
                    val oilList = mutableListOf<Float>()
                    val vegetableList = mutableListOf<Float>()
                    val proteinList = mutableListOf<Float>()
                    val fruitList = mutableListOf<Float>()
                    val carbonList = mutableListOf<Float>()
                    for (document in it) {
                        val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                        if (date.value != null && "${sdf.format(convertDate).split("-")[0]}-" +
                            "${sdf.format(convertDate).split("-")[1]}" ==
                            "${date.value!!.split("-")[0]}-${date.value!!.split("-")[1]}"){
                            items.add(document.toObject(Foodie::class.java))
                            datelist.add(sdfM.format(document.toObject(Foodie::class.java).timestamp))
                        }
                    }
                    val cleanList = datelist.distinct()
                    fireFoodieBack(items)
                    fireDateBack(cleanList)
                    Logger.i("WeeekLinechartViewModel fireDate = ${fireDate.value} cleanList = $cleanList")
                }
        }
    }
}