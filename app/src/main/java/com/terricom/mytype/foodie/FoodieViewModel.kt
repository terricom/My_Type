package com.terricom.mytype.foodie

import androidx.databinding.InverseMethod
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.Logger
import java.sql.Timestamp

class FoodieViewModel: ViewModel() {

    val selectedFood = mutableListOf<String>()

    fun dragToList(food: String) {
        selectedFood.add(food)
    }

    val selectedNutrition = mutableListOf<String>()

    fun dragToListNu(nutrition: String) {
        selectedNutrition.add(nutrition)
        Logger.i("selectedNutrition =$selectedNutrition")
    }

    val water =  MutableLiveData<Long>()
    val oil = MutableLiveData<Long>()
    val vegetable = MutableLiveData<Long>()
    val protein = MutableLiveData<Long>()
    val fruit = MutableLiveData<Long>()
    val carbon = MutableLiveData<Long>()

    @InverseMethod("convertLongToString")
    fun convertStringToLong(value: String): Long {
        return try {
            value.toLong().let {
                when (it) {
                    0L -> 1
                    else -> it
                }
            }
        } catch (e: NumberFormatException) {
            1
        }
    }

    fun convertLongToString(value: Long): String {
        return value.toString()
    }

    val date = MutableLiveData<String>()
    var cleanDate = date.value?.replace(".","-")

    val time = MutableLiveData<String>()


    val db = FirebaseFirestore.getInstance()
    val user = db.collection("Users")

    fun addFoodie(){

        //發文功能
        val foodieContent = hashMapOf(
            "timestamp" to Timestamp.valueOf("${date.value?.replace(".","-")} ${time.value}:00.000000000"),
            "water" to water.value,
            "oil" to oil.value,
            "vegetable" to vegetable.value,
            "protein" to protein.value,
            "fruit" to fruit.value,
            "carbon" to carbon.value,
            "photo" to "",
            "foods" to selectedFood,
            "nutritions" to selectedNutrition
        )

        user.get()
            .addOnSuccessListener { result->
                for (doc in result){
                    if (doc["user_name"]== "Terri 醬"){
                        user.document(doc.id).collection("Foodie").document().set(foodieContent)
                    }
                }

            }


    }


}