package com.terricom.mytype.goalsetting

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.data.source.MyTypeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*

class GoalSettingViewModel(private val myTypeRepository: MyTypeRepository): ViewModel() {

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
    val water =  MutableLiveData<String>()
    val oil = MutableLiveData<String>()
    val vegetable = MutableLiveData<String>()
    val protein = MutableLiveData<String>()
    val fruit = MutableLiveData<String>()
    val carbon = MutableLiveData<String>()
    var weight = MutableLiveData<String>()
    var bodyFat = MutableLiveData<String>()
    var muscle = MutableLiveData<String>()

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

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var deadline: Date? = null

    var goal: LiveData<List<Goal>> = myTypeRepository.getGoal()

    fun addGoal(documentId : String){

        val goalContent = hashMapOf(
            FirebaseKey.TIMESTAMP to FieldValue.serverTimestamp(),
            FirebaseKey.COLUMN_GOAL_DEADLINE to Timestamp(date.value!!.time),
            FirebaseKey.COLUMN_GOAL_WATER to (water.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_GOAL_OIL to (oil.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_GOAL_VEGETABLE to (vegetable.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_GOAL_PROTEIN to (protein.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_GOAL_FRUIT to (fruit.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_GOAL_CARBON to (carbon.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_GOAL_WEIGHT to (weight.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_GOAL_BODY_FAT to (bodyFat.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_GOAL_MUSCLE to (muscle.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_GOAL_CHEER_UP to (cheerUp.value ?: "0.0").toFloat()
        )

        coroutineScope.launch {

            when(documentId){
                "" -> {
                    when(myTypeRepository.isGoalInLocal(UserManager.localGoalId!!)){

                        true -> {
                            when (deadline!!.after(Date())){
                                true -> addGoal2FirebaseFail()
                                false -> {
                                    myTypeRepository.setOrUpdateObjects(FirebaseKey.COLLECTION_GOAL, goalContent, documentId)
                                    addGoal2FirebaseSuccess()
                                }
                            }
                        }
                        false -> {
                            myTypeRepository.setOrUpdateObjects(FirebaseKey.COLLECTION_GOAL, goalContent, documentId)
                            addGoal2FirebaseSuccess()
                        }
                    }
                }
                else -> {
                    myTypeRepository.setOrUpdateObjects(FirebaseKey.COLLECTION_GOAL, goalContent, documentId)
                    addGoal2FirebaseSuccess()
                }
            }
        }
    }
}