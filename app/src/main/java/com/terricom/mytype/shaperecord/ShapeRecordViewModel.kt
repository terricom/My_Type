package com.terricom.mytype.shaperecord

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.tools.toDateFormat
import java.sql.Timestamp
import java.util.*

class ShapeRecordViewModel: ViewModel() {

    private val _date = MutableLiveData<Date>()
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

    private val _recordedDates = MutableLiveData<List<String>>()
    private val recordedDates : LiveData<List<String>>
        get() = _recordedDates

    private fun getRecordedDates(list: List<String>){
        _recordedDates.value = list
    }


    private val _isAddDataShape = MutableLiveData<Boolean>()
    val isAddDataShape : LiveData<Boolean>
        get() = _isAddDataShape

    private fun addShapeSuccess(){
        _isAddDataShape.value = true
    }

    private fun addShapeFail(){
        _isAddDataShape.value = false
    }

    init {
        setDate(Date())
    }

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()


    fun addOrUpdateShape2Firebase(docId: String){

        Logger.i("currentDate.value = ${date.value}")
        //發文功能
        val shapeContent = hashMapOf(
            FirebaseKey.TIMESTAMP to Timestamp(date.value!!.time),
            FirebaseKey.COLUMN_SHAPE_WEIGHT to weight.value,
            FirebaseKey.COLUMN_SHAPE_BODY_WATER to bodyWater.value,
            FirebaseKey.COLUMN_SHAPE_BODY_FAT to bodyFat.value,
            FirebaseKey.COLUMN_SHAPE_MUSCLE to muscle.value,
            FirebaseKey.COLUMN_SHAPE_TDEE to tdee.value,
            FirebaseKey.COLUMN_SHAPE_BODY_AGE to bodyAge.value
        )

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {userDocument ->

                recordedDates.value?.let {

                    when (it.contains(date.value.toDateFormat(FORMAT_YYYY_MM_DD))){

                        true -> addShapeFail()
                        false -> {

                            when (docId){
                                "" -> userDocument.collection(FirebaseKey.COLLECTION_SHAPE).document().set(shapeContent)
                                else -> userDocument.collection(FirebaseKey.COLLECTION_SHAPE).document(docId).set(shapeContent)
                            }
                            addShapeSuccess()
                        }
                    }
                }

            }
        }
    }

    fun getRecordedDates() {

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {userDocument ->

                userDocument.collection(FirebaseKey.COLLECTION_SHAPE)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {
                        val items = mutableListOf<Shape>(
                        )
                        val cleanDates = mutableListOf<String>()
                        for (document in it) {

                            items.add(document.toObject(Shape::class.java))
                            items[items.size-1].docId = document.id
                            cleanDates.add(document.toObject(Shape::class.java).timestamp.toDateFormat(
                                FORMAT_YYYY_MM_DD))
                        }
                        getRecordedDates(cleanDates.distinct())
                    }
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