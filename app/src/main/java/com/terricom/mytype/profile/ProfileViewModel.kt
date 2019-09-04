package com.terricom.mytype.profile

import androidx.databinding.InverseMethod
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.data.UserManager

class ProfileViewModel: ViewModel() {

    val userName = UserManager.name
    val userPic = UserManager.picture
    val userUid = UserManager.uid

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
        val menuContentSet = hashMapOf(
            "water" to water.value as Float,
            "oil" to oil.value as Float,
            "vegetable" to vegetable.value as Float,
            "protein" to protein.value as Float,
            "fruit" to fruit.value as Float,
            "carbon" to carbon.value as Float,
            "weight" to weight.value as Float,
            "bodyfat" to bodyFat.value as Float,
            "muscle" to muscle.value as Float
        )
        val menuContent = hashMapOf<String, Any>(
            "water" to water.value as Float,
            "oil" to oil.value as Float,
            "vegetable" to vegetable.value as Float,
            "protein" to protein.value as Float,
            "fruit" to fruit.value as Float,
            "carbon" to carbon.value as Float,
            "weight" to weight.value as Float,
            "bodyfat" to bodyFat.value as Float,
            "muscle" to muscle.value as Float
        )
        user.get()
            .addOnSuccessListener { result->
                for (doc in result){
                    if (doc.id == userUid){
                        user.document(doc.id).collection("Goal").document().set(menuContentSet)
//                        user.document(doc.id).collection("Goal").document().update(menuContent).addOnCompleteListener {  }
                    }
                }

            }


    }
    fun addGoal(){

        //發文功能
        val menuContent = hashMapOf<String, Any>(
            "water" to water.value as Float,
            "oil" to oil.value as Float,
            "vegetable" to vegetable.value as Float,
            "protein" to protein.value as Float,
            "fruit" to fruit.value as Float,
            "carbon" to carbon.value as Float,
            "weight" to weight.value as Float,
            "bodyfat" to bodyFat.value as Float,
            "muscle" to muscle.value as Float
        )

        user.get()
            .addOnSuccessListener { result->
                for (doc in result){
                    if (doc.id == userUid){
                        user.document(doc.id).collection("Goal").document().update(menuContent).addOnCompleteListener {  }
                    }
                }

            }
    }



}