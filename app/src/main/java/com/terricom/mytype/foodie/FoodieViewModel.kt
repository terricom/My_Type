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

    val water =  MutableLiveData<Float>()
    val oil = MutableLiveData<Float>()
    val vegetable = MutableLiveData<Float>()
    val protein = MutableLiveData<Float>()
    val fruit = MutableLiveData<Float>()
    val carbon = MutableLiveData<Float>()

    fun convertStringToFloat(string: String): Float {
        return try {
            string.toFloat()
        } catch (nfe: NumberFormatException) {
            0.0f
        }
    }

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()


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

    fun clearData(){
        water.value = 0.0f
        oil.value = 0.0f
        vegetable.value = 0.0f
        protein.value = 0.0f
        fruit.value = 0.0f
        carbon.value = 0.0f
    }


}