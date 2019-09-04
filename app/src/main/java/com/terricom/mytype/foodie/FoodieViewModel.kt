package com.terricom.mytype.foodie

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.Logger
import com.terricom.mytype.data.UserManager
import java.sql.Timestamp

class FoodieViewModel: ViewModel() {

    val userUid = UserManager.uid

    var selectedFood = mutableListOf<String>()
    var selectedNutrition = mutableListOf<String>()

    val _userFoodList = MutableLiveData<List<String>>()
    val userFoodList : LiveData<List<String>>
        get() = _userFoodList

    val _userNuList = MutableLiveData<List<String>>()
    val userNuList : LiveData<List<String>>
        get() = _userNuList

    fun getFoodlist(foodlist: List<String>){
        _userFoodList.value = foodlist
    }

    fun getNulist(nulist: List<String>){
        _userNuList.value = nulist
    }


    fun dragToList(food: String) {
        selectedFood.add(food)
    }

    fun dragToListNu(nutrition: String) {
        selectedNutrition.add(nutrition)
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
                Logger.i("FoodieViewModel userUid =$userUid")
                for (doc in result){
                    if (doc.id == userUid){
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

    init {
        if (userUid != null){
            getFoodAndNuList()
        }
    }

    fun getFoodAndNuList(){

        user.get()
            .addOnSuccessListener { result ->
                for (doc in result){
                    if (doc.id == userUid){
                        var firebaseFoodlist = doc["foodlist"] as List<String>
                        getFoodlist(firebaseFoodlist)
                        var firebaseNulist = doc["nutritionlist"] as List<String>
                        getNulist(firebaseNulist)
                    }
                }

            }
    }


}