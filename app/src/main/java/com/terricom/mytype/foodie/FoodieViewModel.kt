package com.terricom.mytype.foodie

import android.net.Uri
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.Logger
import com.terricom.mytype.data.UserMT
import com.terricom.mytype.data.UserManager
import java.sql.Time
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*




class FoodieViewModel: ViewModel() {

    val userUid = UserManager.uid

    var selectedFood = mutableListOf<String>()
    var selectedNutrition = mutableListOf<String>()

    val _userFoodList = MutableLiveData<List<String>>()
    val userFoodList : LiveData<List<String>>
        get() = _userFoodList

    val _addPhoto = MutableLiveData<Boolean>()
    val addPhoto : LiveData<Boolean>
        get() = _addPhoto

    fun addPhoto(){
        _addPhoto.value = true
    }

    val addFood = MutableLiveData<String>()
    val addNutrition = MutableLiveData<String>()

    val newFuList = mutableListOf<String>()
    val newNuList = mutableListOf<String>()


    val _userNuList = MutableLiveData<List<String>>()
    val userNuList : LiveData<List<String>>
        get() = _userNuList

    fun getFoodlist(foodlist: List<String>){
        _userFoodList.value = foodlist
        for (food in foodlist){
            newFuList.add(food)
        }
    }

    fun getNulist(nulist: List<String>){
        _userNuList.value = nulist
        for (nutrition in nulist){
            newNuList.add(nutrition)
        }
    }


    fun dragToList(food: String) {
        Logger.i("dragToList food =$food")
        selectedFood.add(food)
    }

    fun dragOutList(food: String) {
        Logger.i("dragToList food =$food")
        selectedFood.remove(food)
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


    val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    fun setDate(date: Date){
        _date.value = date
        _time.value = Time(date.time)
    }


    val _time = MutableLiveData<Time>()
    val time : LiveData<Time>
        get() = _time

    private val _photoUri = MutableLiveData<Uri>()
    val photoUri: LiveData<Uri>
        get() = _photoUri

    fun setPhoto(photo: Uri){
        _photoUri.value = photo
        Logger.i("photouri get = $photo")
    }

    val _addNewFoodChecked = MutableLiveData<Boolean>()
    val addNewFoodChecked : LiveData<Boolean>
        get() = _addNewFoodChecked

    fun checkedAddNewFood(){
        _addNewFoodChecked.value = true
    }

    fun unCheckedAddNewFood(){
        _addNewFoodChecked.value = false
    }

    val db = FirebaseFirestore.getInstance()
    val user = db.collection("Users")

    fun addFoodie(){

        Logger.i("Timestamp Format = \"${sdf.format(date.value)} ${time.value}:00.000000000\")")
        //發文功能
        val foodieContent = hashMapOf(
            "timestamp" to Timestamp.valueOf("${sdf.format(date.value)} ${time.value}.000000000"),
            "water" to water.value,
            "oil" to oil.value,
            "vegetable" to vegetable.value,
            "protein" to protein.value,
            "fruit" to fruit.value,
            "carbon" to carbon.value,
            "photo" to photoUri.value.toString(),
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

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfT = SimpleDateFormat("HH:mm")
    val currentDate = sdf.format(Date())
    val currentTime = sdfT.format(Date())

    init {
        if (userUid != null){
            getFoodAndNuList()
        }
        setDate(Date())
        _addPhoto.value = false
        unCheckedAddNewFood()
    }

    fun getFoodAndNuList(){

        user.get()
            .addOnSuccessListener { result ->
                for (doc in result){
                    if (doc.id == userUid){
                        val user = doc.toObject(UserMT::class.java)
                        if (user.foodlist != null){
                            var firebaseFoodlist: List<String> = doc["foodlist"] as List<String>
                            getFoodlist(firebaseFoodlist)
                        }
                        if (user.nutritionlist != null){
                            var firebaseNulist: List<String> = doc["nutritionlist"] as List<String>
                            getNulist(firebaseNulist)
                        }
                    }
                }

            }
    }

    fun updateFoodAndNuList(){

        user.get()
            .addOnSuccessListener { result ->
                for (doc in result){
                    if (doc.id == userUid){
                        val user = doc.toObject(UserMT::class.java)
//                        if (user.foodlist == null){
                            db.collection("Users").document(doc.id).update("foodlist", newFuList).addOnCompleteListener{}
//                        }
//                        if (user.nutritionlist == null){
                            db.collection("Users").document(doc.id).update("nutritionlist", newNuList).addOnCompleteListener {  }
//                        }
                    }
                }

            }
    }




}