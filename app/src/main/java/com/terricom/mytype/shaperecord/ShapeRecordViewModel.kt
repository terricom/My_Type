package com.terricom.mytype.shaperecord

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.Logger
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.UserManager
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ShapeRecordViewModel: ViewModel() {

    val userUid = UserManager.uid
    val sdf = SimpleDateFormat("yyyy-MM-dd")

    val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    fun setDate(date: Date){
        _date.value = date
    }

    var weight = MutableLiveData<Float>()
    var bodyWater = MutableLiveData<Float>()
    var bodyFat = MutableLiveData<Float>()
    var muscle = MutableLiveData<Float>()
    var tdee = MutableLiveData<Float>()
    var bodyAge = MutableLiveData<Float>()

    fun convertStringToFloat(string: String): Float {
        return try {
            string.toFloat()
        } catch (nfe: NumberFormatException) {
            0.0f
        }
    }

    val _fireShapeM = MutableLiveData<List<Shape>>()
    val fireShapeM : LiveData<List<Shape>>
        get() = _fireShapeM

    fun fireShapeBackM (shape: List<Shape>){
        _fireShapeM.value = shape
    }

    val _recordedDates = MutableLiveData<List<String>>()
    val recordedDates : LiveData<List<String>>
        get() = _recordedDates

    fun getRecordedDates(list: List<String>){
        _recordedDates.value = list
    }


    val _addShapeResult = MutableLiveData<Boolean>()
    val addShapeResult : LiveData<Boolean>
        get() = _addShapeResult

    fun addShapeSuccess(){
        _addShapeResult.value = true
    }

    fun addShapeFail(){
        _addShapeResult.value = false
    }

    val _updateShape = MutableLiveData<Shape>()
    val updateShape : LiveData<Shape>
        get() = _updateShape

    fun updateShape(shape: Shape){
        _updateShape.value = shape
    }


    init {
        setDate(Date())
    }

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()

    val db = FirebaseFirestore.getInstance()
    val user = db.collection("Users")

    fun addShape(){

        com.terricom.mytype.Logger.i("date.value = ${date.value}")
        //發文功能
        val shapeContent = hashMapOf(
            "timestamp" to Timestamp(date.value!!.time),
            "weight" to weight.value,
            "bodyWater" to bodyWater.value,
            "bodyFat" to bodyFat.value,
            "muscle" to muscle.value,
            "tdee" to tdee.value,
            "bodyAge" to bodyAge.value
        )

        if (userUid!!.isNotEmpty()) {
            user.get()
                .addOnSuccessListener { result ->
//                    for (doc in result) {
                        if (recordedDates.value!!.contains("${sdf.format(date.value)}")) {
                            addShapeFail()
                        } else {
                            user.document(userUid).collection("Shape").document().set(shapeContent)
                            addShapeSuccess()
                        }
//                    }

                }
        }

    }


    fun getToday(): Boolean {
        val items = mutableListOf<Shape>()

        if (userUid!!.isNotEmpty()){
            val shapeRecord = user
                .document(userUid)
                .collection("Shape")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 00:00:00.000000000"))
                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 23:59:59.000000000"))

            shapeRecord
                .get()
                .addOnSuccessListener {
                    for (document in it) {
                        val convertDate = java.sql.Date(document.toObject(Shape::class.java).timestamp!!.time)
                            items.add(document.toObject(Shape::class.java))
                            items[items.size-1].docId = document.id
                    }
                }
        }
        Logger.i("items = $items")
        Logger.i("getToday() items = $items")
        Logger.i("date = ${sdf.format(date.value)}  ")
        return items.size == 0
    }

    fun getThisMonth() {
        if (userUid!!.isNotEmpty()){
            val shapeRecord = user
                .document(userUid)
                .collection("Shape")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            shapeRecord
                .get()
                .addOnSuccessListener {
                    val items = mutableListOf<Shape>()
                    val cleanDates = mutableListOf<String>()
                    for (document in it) {
                        val convertDate = java.sql.Date(document.toObject(Shape::class.java).timestamp!!.time)
                        if (date.value != null && "${sdf.format(convertDate).split("-")[0]}-" +
                            "${sdf.format(convertDate).split("-")[1]}" ==
                            "${sdf.format(date.value)!!.split("-")[0]}-" +
                            "${sdf.format(date.value)!!.split("-")[1]}"){
                            items.add(document.toObject(Shape::class.java))
                            items[items.size-1].docId = document.id
                            cleanDates.add(sdf.format(Date(document.toObject(Shape::class.java).timestamp!!.time)))
                        }
                    }
                    getRecordedDates(cleanDates.distinct())
                    fireShapeBackM(items)
                    Logger.i("fireFoodieM =${fireShapeM.value}")
                }
        }
    }

    fun clearData(){
        weight.value = 0.0f
        bodyAge.value = 0.0f
        bodyFat.value = 0.0f
        bodyWater.value = 0.0f
        tdee.value = 0.0f
        muscle.value = 0.0f
    }



}