package com.terricom.mytype.goalsetting

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.UserManager
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class GoalSettingViewModel: ViewModel() {
    val userUid = UserManager.uid
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfDetail = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    val _addGoal = MutableLiveData<Boolean>()
    val addGoal: LiveData<Boolean>
        get() = _addGoal

    fun addGoal2FirebaseSuccess(){
        _addGoal.value = true
    }
    fun addGoal2FirebaseFail(){
        _addGoal.value = false
    }
    fun addGoal2FirebaseComplete(){
        _addGoal.value = null
    }

    val cheerUp = MutableLiveData<String>()
    val water =  MutableLiveData<Float>()
    val oil = MutableLiveData<Float>()
    val vegetable = MutableLiveData<Float>()
    val protein = MutableLiveData<Float>()
    val fruit = MutableLiveData<Float>()
    val carbon = MutableLiveData<Float>()
    var weight = MutableLiveData<Float>()
    var bodyFat = MutableLiveData<Float>()
    var muscle = MutableLiveData<Float>()

    fun setDate(date: Date){
        _date.value = date
    }

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

    init {
        setDate(Date())
    }

    fun addGoal(){

        //發文功能
        val goalContent = hashMapOf(
            "timestamp" to Timestamp.valueOf("${sdfDetail.format(Date().time)}.000000000"),
            "deadline" to Timestamp.valueOf("${sdf.format(date.value)} 12:00:00.000000000"),
            "water" to water.value,
            "oil" to oil.value,
            "vegetable" to vegetable.value,
            "protein" to protein.value,
            "fruit" to fruit.value,
            "carbon" to carbon.value,
            "weight" to weight.value,
            "bodyFat" to bodyFat.value,
            "muscle" to muscle.value,
            "cheerUp" to cheerUp.value
        )

        if (userUid!!.isNotEmpty()){

            val goal = user
                .document(userUid).collection("Goal")
                .orderBy("deadline", Query.Direction.DESCENDING)
//                .whereLessThanOrEqualTo("deadline", Timestamp.valueOf("${sdf.format(date.value)} 23:59:59.000000000"))
//                .whereGreaterThanOrEqualTo("deadline", Timestamp.valueOf("${sdf.format(date.value)} 00:00:00.000000000"))

            goal.get()
                .addOnSuccessListener { result->
                    if (result.isEmpty){
                        user.document(userUid).collection("Goal").document().set(goalContent)
                        addGoal2FirebaseSuccess()
                    }else{
                        val items = mutableListOf<Goal>()
                    for (doc in result){
                        val goal = doc.toObject(Goal::class.java)
                        items.add(goal)
                    }
                    if (items[0].deadline!!.before(Date())){
                        user.document(userUid).collection("Goal").document().set(goalContent)
                        addGoal2FirebaseSuccess()
                    } else {
                        addGoal2FirebaseFail()
                    }
                    }

                }
        }


    }
}