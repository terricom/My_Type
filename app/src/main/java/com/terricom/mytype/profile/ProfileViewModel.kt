package com.terricom.mytype.profile

import androidx.databinding.InverseMethod
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.data.UserManager

class ProfileViewModel: ViewModel() {

    val userName = UserManager.name
    val userPic = UserManager.picture

    val outlineProvider = ProfileAvatarOutlineProvider()

    val water =  MutableLiveData<Float>()
    val oil = MutableLiveData<Float>()
    val vegetable = MutableLiveData<Float>()
    val protein = MutableLiveData<Float>()
    val fruit = MutableLiveData<Float>()
    val carbon = MutableLiveData<Float>()
    var weight = MutableLiveData<Float>()
    var bodyFat = MutableLiveData<Float>()
    var muscle = MutableLiveData<Float>()

    val date = MutableLiveData<String>()
    val goal = MutableLiveData<String>()

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

    fun addMenu(){

        //發文功能
        val menuContent = hashMapOf(
            "water" to water.value,
            "oil" to oil.value,
            "vegetable" to vegetable.value,
            "protein" to protein.value,
            "fruit" to fruit.value,
            "carbon" to carbon.value,
            "weight" to weight.value,
            "bodyfat" to bodyFat.value,
            "muscle" to muscle.value
        )

        user.get()
            .addOnSuccessListener { result->
                for (doc in result){
                    if (doc["user_name"]== "Terri 醬"){
                        user.document(doc.id).set(menuContent)
                    }
                }

            }


    }
    fun addGoal(){

        //發文功能
        val menuContent = hashMapOf(
            "water" to water.value,
            "oil" to oil.value,
            "vegetable" to vegetable.value,
            "protein" to protein.value,
            "fruit" to fruit.value,
            "carbon" to carbon.value,
            "weight" to weight.value,
            "bodyfat" to bodyFat.value,
            "muscle" to muscle.value
        )

        user.get()
            .addOnSuccessListener { result->
                for (doc in result){
                    if (doc["user_name"]== "Terri 醬"){
                        user.document(doc.id).set(menuContent)
                    }
                }

            }


    }



}