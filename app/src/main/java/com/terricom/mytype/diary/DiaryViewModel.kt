package com.terricom.mytype.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.*
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_FOODIE
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_GOAL
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_SHAPE
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_SLEEP
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.FORMAT_HH_MM
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.tools.toDateFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*

class DiaryViewModel(private val myTypeRepository: MyTypeRepository): ViewModel() {

    val outlineProvider = CardAvatarOutlineProvider()

    private val _date = MutableLiveData<Date>()
    val date : LiveData<Date>
        get() = _date

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean>
        get() = _status

    private val _dataFoodieFromFirebase = MutableLiveData<List<Foodie>>()
    val dataFoodieFromFirebase : LiveData<List<Foodie>>
        get() = _dataFoodieFromFirebase

    private fun setDataFoodieFromFirebase (foo: List<Foodie>?){
        _dataFoodieFromFirebase.value = foo
    }

    private val _dataShapeFromFirebase = MutableLiveData<Shape>()
    val dataShapeFromFirebase : LiveData<Shape>
        get() = _dataShapeFromFirebase

    private fun setDataShapeFromFIrebase (shape: Shape?){
        _dataShapeFromFirebase.value = shape
    }

    private val _dataSleepFromFirebase = MutableLiveData<Sleep>()
    val dataSleepFromFirebase : LiveData<Sleep>
        get() = _dataSleepFromFirebase

    private fun setDataSleepFromFirebase (sleep: Sleep?){
        _dataSleepFromFirebase.value = sleep
    }

    fun setCurrentDate(date: Date){
        _date.value = date
    }

    fun clearDataShapeFromFirebase(){
        _dataShapeFromFirebase.value = null
    }

    fun clearDataSleepFromFirebase(){
        _dataSleepFromFirebase.value = null
    }

    private val _isCalendarClicked = MutableLiveData<Boolean>()
    val isCalendarClicked : LiveData<Boolean>
        get() = _isCalendarClicked

    fun calendarClicked(){
        _isCalendarClicked.value = true
    }

    fun calendarClickedAgain(){
        _isCalendarClicked.value = false
    }

    private val _isGetPuzzle = MutableLiveData<Boolean>()
    val isGetPuzzle : LiveData<Boolean>
        get() = _isGetPuzzle

    private fun getPuzzleOldUser(){
        _isGetPuzzle.value = true
    }

    private fun getPuzzleNewUser(){
        _isGetPuzzle.value = false
    }

    private val _isCallDeleteAction = MutableLiveData<Boolean>()
    val isCallDeleteAction : LiveData<Boolean>
        get() = _isCallDeleteAction

    private fun callDeleteAction(){
        _isCallDeleteAction.value = true
    }

    private fun finishCallDeleteAction(){
        _isCallDeleteAction.value = false
    }


    init {
        calendarClickedAgain()
        finishCallDeleteAction()
    }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun getAndSetFoodieShapeSleepToday() {

        coroutineScope.launch {

            val shapeResult = myTypeRepository.getObjects<Shape>(COLLECTION_SHAPE,
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_daybegin,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                ),
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_dayend,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                )
            )

            when (shapeResult) {
                is Result.Success -> setDataShapeFromFIrebase(shapeResult.data.firstOrNull())
            }

            val sleepResult = myTypeRepository.getObjects<Sleep>(COLLECTION_SLEEP,
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_daybegin,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                ),
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_dayend,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                )
            )

            when (sleepResult) {
                is Result.Success -> setDataSleepFromFirebase(sleepResult.data.firstOrNull())
            }

            val foodieResult = myTypeRepository.getObjects<Foodie>(
                COLLECTION_FOODIE,
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_daybegin,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                ),
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_dayend,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                )
            )

            when (foodieResult) {
                is Result.Success -> {
                    foodieResult.data.forEach { foodie ->
                        totalWater = totalWater.plus(foodie.water ?:0f)
                        totalFruit = totalFruit.plus(foodie.fruit ?: 0f)
                        totalOil = totalOil.plus(foodie.oil ?: 0f)
                        totalVegetable = totalVegetable.plus(foodie.vegetable ?: 0f)
                        totalProtein = totalProtein.plus(foodie.protein ?: 0f)
                        totalCarbon = totalCarbon.plus(foodie.carbon ?: 0f)
                    }
                    setDataFoodieFromFirebase(foodieResult.data)
                    _status.value = true
                }
            }
            updatePuzzle()
        }
    }

    var totalWater = 0.0f
    var totalOil = 0.0f
    var totalVegetable = 0.0f
    var totalProtein = 0.0f
    var totalFruit = 0.0f
    var totalCarbon = 0.0f

    private val _listQueryFoodieResult = MutableLiveData<FoodieList>()
    val listQueryFoodieResult : LiveData<FoodieList>
        get() = _listQueryFoodieResult

    private fun setQueryResult(queryResult : List<Foodie>, key: String){
        _listQueryFoodieResult.value = FoodieList(queryResult, key)
    }

    fun getTime(timestamp: Date):String{
            return java.sql.Date(timestamp.time).toDateFormat(FORMAT_HH_MM)
    }

    fun deleteFoodie(foodie: Foodie){

        if (UserManager.isLogin()){

            coroutineScope.launch {

                myTypeRepository.deleteObjects(COLLECTION_FOODIE, foodie)

                callDeleteAction()
            }
        }
    }

    private fun updatePuzzle() {

        if (UserManager.isLogin()){

            coroutineScope.launch {

                when(myTypeRepository.updatePuzzle()){
                    0 -> getPuzzleNewUser()
                    1 -> getPuzzleOldUser()
                }
                Logger.i("myTypeRepository.updatePuzzle() = ${myTypeRepository.updatePuzzle()}")
            }
        }
    }

    fun queryFoodie(key: String, type: String) {

        coroutineScope.launch {

            when (val result = myTypeRepository.queryFoodie(key, type)) {
                is Result.Success -> setQueryResult(result.data, key)
            }
        }
    }

    fun getGoal(){

        coroutineScope.launch {

            when (val result = myTypeRepository.getObjects<Goal>(COLLECTION_GOAL, Timestamp(946656000), Timestamp(4701859200))) {
                is Result.Success -> {

                    when(myTypeRepository.isGoalInLocal(UserManager.localGoalId!!)){

                        false -> {
                            if (result.data.isNotEmpty()){
                                myTypeRepository.insertGoal(result.data[0])
                                UserManager.localGoalId = result.data.first().docId
                            }
                        }
                        result.data.isNotEmpty() -> {
                            myTypeRepository.updateGoal(result.data[0])
                            UserManager.localGoalId = result.data.first().docId
                        }
                    }
                }
            }
        }
    }
}