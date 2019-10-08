package com.terricom.mytype.goalsetting

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD_HH_MM_SS_FFFFFFFFF
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.toDateFormat
import java.sql.Timestamp
import java.util.*

class GoalSettingViewModel: ViewModel() {
    val userUid = UserManager.uid

    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    private val _isAddGoal2Firebase = MutableLiveData<Boolean>()
    val isAddGoal2Firebase: LiveData<Boolean>
        get() = _isAddGoal2Firebase

    private fun addGoal2FirebaseSuccess(){
        _isAddGoal2Firebase.value = true
    }
    private fun addGoal2FirebaseFail(){
        _isAddGoal2Firebase.value = false
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
            0f
        }
    }

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()

    init {
        setDate(Date())
    }


    fun addGoal(documentId : String){

        val goalContent = hashMapOf(
            FirebaseKey.TIMESTAMP to FieldValue.serverTimestamp(),
            FirebaseKey.COLUMN_GOAL_DEADLINE to Timestamp.valueOf(
                date.value.toDateFormat(
                    FORMAT_YYYY_MM_DD_HH_MM_SS_FFFFFFFFF
                )
            ),
            FirebaseKey.COLUMN_GOAL_WATER to water.value,
            FirebaseKey.COLUMN_GOAL_OIL to oil.value,
            FirebaseKey.COLUMN_GOAL_VEGETABLE to vegetable.value,
            FirebaseKey.COLUMN_GOAL_PROTEIN to protein.value,
            FirebaseKey.COLUMN_GOAL_FRUIT to fruit.value,
            FirebaseKey.COLUMN_GOAL_CARBON to carbon.value,
            FirebaseKey.COLUMN_GOAL_WEIGHT to weight.value,
            FirebaseKey.COLUMN_GOAL_BODY_FAT to bodyFat.value,
            FirebaseKey.COLUMN_GOAL_MUSCLE to muscle.value,
            FirebaseKey.COLUMN_GOAL_CHEER_UP to cheerUp.value
        )

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {

                when (documentId){

                    "" -> {

                        it.collection(FirebaseKey.COLLECTION_GOAL)
                            .orderBy(FirebaseKey.COLUMN_GOAL_DEADLINE, Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener { result->

                                val items = mutableListOf<Goal>()
                                for (doc in result){
                                    val goal = doc.toObject(Goal::class.java)
                                    items.add(goal)
                                }
                                if (items.isEmpty()){

                                    it.collection(FirebaseKey.COLLECTION_GOAL).document().set(goalContent)
                                    addGoal2FirebaseSuccess()

                                }else {
                                    //現在日期早於最近一筆目標日期
                                    if (items[0].deadline!!.time > Date().time){
                                        addGoal2FirebaseFail()
                                    } else {
                                        it.collection(FirebaseKey.COLLECTION_GOAL).document().set(goalContent)
                                        addGoal2FirebaseSuccess()
                                    }
                                }
                            }
                    }

                    else -> {

                        it.collection(FirebaseKey.COLLECTION_GOAL).document(documentId).update(goalContent)
                        addGoal2FirebaseSuccess()
                    }
                }
            }
        }
    }

}