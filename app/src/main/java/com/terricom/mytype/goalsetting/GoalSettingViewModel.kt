package com.terricom.mytype.goalsetting

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
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

    val _updateGoal = MutableLiveData<Goal>()
    val updateGoal : LiveData<Goal>
        get() = _updateGoal

    fun updateGoal(goal: Goal){
        _updateGoal.value = goal
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
    val goalDocId = MutableLiveData<String>()

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
            "timestamp" to FieldValue.serverTimestamp(),
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

            goal.get()
                .addOnSuccessListener { result->
                    val items = mutableListOf<Goal>()
                    for (doc in result){
                        val goal = doc.toObject(Goal::class.java)
                        items.add(goal)
                    }
                    if (items.isEmpty()){
                        user.document(userUid).collection("Goal").document().set(goalContent)
                        addGoal2FirebaseSuccess()
                    }else {
                        if (items[0].deadline!!.time > Date().time //現在日期早於最近一筆目標日期
//                            && sdf.format(items[0].deadline)!= sdf.format(date.value)
//                            && sdf.format(items[0].deadline)!= sdf.format(Date())
                        ){
                            addGoal2FirebaseFail()
                        } else {
                            user.document(userUid).collection("Goal").document().set(goalContent)
                            addGoal2FirebaseSuccess()
                        }
                    }

                }
        }


    }

    fun adjustGoal(){

        //發文功能
        val goalContent = hashMapOf(
            "timestamp" to FieldValue.serverTimestamp(),
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

            goal.get()
                .addOnSuccessListener { result->
                    user.document(userUid).collection("Goal").document(goalDocId.value!!).set(goalContent)
                    addGoal2FirebaseSuccess()
                }
        }


    }
}