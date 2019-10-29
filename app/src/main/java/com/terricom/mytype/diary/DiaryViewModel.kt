package com.terricom.mytype.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.*
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_FOODIE
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_SHAPE
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_SLEEP
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

class DiaryViewModel(private val firebaseRepository: FirebaseRepository): ViewModel() {

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

    private fun setDataFoodieFromFirebase (foo: List<Foodie>){
        _dataFoodieFromFirebase.value = foo
    }

    private val _dataShapeFromFirebase = MutableLiveData<Shape>()
    val dataShapeFromFirebase : LiveData<Shape>
        get() = _dataShapeFromFirebase

    private fun setDataShapeFromFIrebase (shape: Shape){
        _dataShapeFromFirebase.value = shape
    }

    private val _dataSleepFromFirebase = MutableLiveData<Sleep>()
    val dataSleepFromFirebase : LiveData<Sleep>
        get() = _dataSleepFromFirebase

    private fun setDataSleepFromFirebase (sleep: Sleep){
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

            val shape = firebaseRepository.getObjects(
                COLLECTION_SHAPE,
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_daybegin,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                ),
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_dayend,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                )
            )

            if (shape.isNotEmpty()){

                setDataShapeFromFIrebase(shape[0] as Shape)
            }

            val sleep = firebaseRepository.getObjects(
                COLLECTION_SLEEP,
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_daybegin,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                ),
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_dayend,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                )
            )

            if (sleep.isNotEmpty()){

                setDataSleepFromFirebase(sleep[0] as Sleep)
            }

            val foodieList = firebaseRepository.getObjects(
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

            if (foodieList.isNotEmpty()){

                for(foodie in foodieList as List<Foodie>){

                    totalWater = totalWater.plus(foodie.water ?:0f)
                    totalFruit = totalFruit.plus(foodie.fruit ?: 0f)
                    totalOil = totalOil.plus(foodie.oil ?: 0f)
                    totalVegetable = totalVegetable.plus(foodie.vegetable ?: 0f)
                    totalProtein = totalProtein.plus(foodie.protein ?: 0f)
                    totalCarbon = totalCarbon.plus(foodie.carbon ?: 0f)
                }

                setDataFoodieFromFirebase(foodieList)
            } else {
                setDataFoodieFromFirebase(emptyList())
            }
            updatePuzzle()
            _status.value = true

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
        _listQueryFoodieResult.value = FoodieList(queryResult,key)
    }

    fun getTime(timestamp: Date):String{
            return java.sql.Date(timestamp.time).toDateFormat(FORMAT_HH_MM)
    }

    fun deleteFoodie(foodie: Foodie){

        if (UserManager.isLogin()){

            coroutineScope.launch {

                firebaseRepository.deleteObjects(COLLECTION_FOODIE, foodie)

                callDeleteAction()
            }
        }
    }

    private fun updatePuzzle() {

        if (UserManager.isLogin()){

            coroutineScope.launch {

                when(firebaseRepository.updatePuzzle()){
                    0 -> getPuzzleNewUser()
                    1 -> getPuzzleOldUser()
                }
                Logger.i("firebaseRepository.updatePuzzle() = ${firebaseRepository.updatePuzzle()}")
            }
        }

//            UserManager.USER_REFERENCE?.let {user ->
//
//                user.collection(COLLECTION_FOODIE)
//                    .orderBy(TIMESTAMP,
//                        Query.Direction.DESCENDING
//                    )
//                    .get()
//                    .addOnSuccessListener {
//                        val dates = mutableListOf<String>()
//                        val items = mutableListOf<Foodie>()
//                        for (document in it) {
//
//                            dates.add(java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time).toDateFormat(
//                                FORMAT_YYYY_MM_DD
//                            ))
//                            items.add(document.toObject(Foodie::class.java))
//                        }
//                        if (dates.distinct().size % 7 == 0){
//
//                            //全新使用者
//                            user.collection(COLLECTION_PUZZLE)
//                                .orderBy(TIMESTAMP,
//                                    Query.Direction.DESCENDING
//                                )
//                                .get()
//                                .addOnSuccessListener {
//
//                                    val puzzleAll = mutableListOf<Puzzle>()
//                                    for (document in it) {
//
//                                        puzzleAll.add(document.toObject(Puzzle::class.java))
//                                        puzzleAll[puzzleAll.lastIndex].docId = document.id
//                                    }
//
//                                    Logger.i("puzzleAll.size = ${puzzleAll.size}")
//                                    when (dates.size){
//                                        0 -> {
//                                            if (UserManager.getPuzzleNewUser == "0"  && puzzleAll.size == 0){
//
//                                                UserManager.getPuzzleNewUser = UserManager.getPuzzleNewUser.toString().toInt().plus(1).toString()
//
//                                                user.collection(COLLECTION_PUZZLE).document().set(
//                                                    hashMapOf(
//                                                        COLUMN_PUZZLE_POSITION to listOf((0..14).random()),
//                                                        COLUMN_PUZZLE_IMGURL to PuzzleImg.values()[0].value,
//                                                        COLUMN_PUZZLE_RECORDEDDATES to listOf(Date().toDateFormat(
//                                                            FORMAT_YYYY_MM_DD
//                                                        )),
//                                                        TIMESTAMP to FieldValue.serverTimestamp()
//                                                    )
//                                                )
//
//                                                getPuzzleNewUser()
//                                            } else if (UserManager.getPuzzleNewUser == "1"  && puzzleAll.size == 1){
//
//                                                UserManager.getPuzzleNewUser = UserManager.getPuzzleNewUser.toString().toInt().plus(1).toString()
//                                                getPuzzleNewUser()
//                                            }
//                                        }
//                                        else -> {
//                                            getPuzzleOldUser()
//                                        }
//                                    }
//                                }
//                        }
//                    }
//
//                }
//            }
    }

    fun queryFoodie(key: String, type: String) {

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {user ->

                user.collection(COLLECTION_FOODIE)
                    .whereArrayContains(type, key)
                    .get()
                    .addOnSuccessListener {

                        val dates = mutableListOf<String>()
                        val items = mutableListOf<Foodie>()
                        for (document in it) {

                            dates.add(java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time).toDateFormat(
                                FORMAT_YYYY_MM_DD
                            ))
                            items.add(document.toObject(Foodie::class.java))
                            items[items.lastIndex].docId = document.id
                        }
                        if (!items.isNullOrEmpty()){

                            setQueryResult(items.asReversed(), key)
                        }
                    }
            }

        }

    }

}