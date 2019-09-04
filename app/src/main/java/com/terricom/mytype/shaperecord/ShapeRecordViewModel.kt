package com.terricom.mytype.shaperecord

import androidx.databinding.InverseMethod
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ShapeRecordViewModel: ViewModel() {

    val date = MutableLiveData<String>()

    fun upDate(update: String?){
        date.value = update
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

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()

    val db = FirebaseFirestore.getInstance()
    val user = db.collection("Users")

    fun addShape(){

        com.terricom.mytype.Logger.i("date.value = ${date.value}")
        //發文功能
        val shapeContent = hashMapOf(
            "timestamp" to date.value,
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

    fun clearData(){
        weight.value = 0.0f
        bodyAge.value = 0.0f
        bodyFat.value = 0.0f
        bodyWater.value = 0.0f
        tdee.value = 0.0f
        muscle.value = 0.0f
    }



}