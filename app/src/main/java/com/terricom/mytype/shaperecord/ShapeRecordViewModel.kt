package com.terricom.mytype.shaperecord

import androidx.databinding.InverseMethod
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp

class ShapeRecordViewModel: ViewModel() {

    val date = MutableLiveData<String>()


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

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()

    val db = FirebaseFirestore.getInstance()
    val user = db.collection("Users")

    fun addShape(){

        //發文功能
        val shapeContent = hashMapOf(
            "timestamp" to Timestamp.valueOf("${date.value}"),
            "weight" to weight.value,
            "bodyWater" to bodyWater.value,
            "bodyFat" to bodyFat.value,
            "muscle" to muscle.value,
            "tdee" to tdee.value,
            "bodyAge" to bodyAge.value
        )

        user.get()
            .addOnSuccessListener { result->
                for (doc in result){
                    if (doc["user_name"]== "Terri 醬"){
                        user.document(doc.id).collection("Shape").document().set(shapeContent)
                    }
                }

            }


    }



}