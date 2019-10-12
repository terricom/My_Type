package com.terricom.mytype.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.*
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_FOODIE
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_PUZZLE
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_PUZZLE_IMGURL
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_PUZZLE_POSITION
import com.terricom.mytype.data.FirebaseKey.Companion.COLUMN_PUZZLE_RECORDEDDATES
import com.terricom.mytype.data.FirebaseKey.Companion.TIMESTAMP
import com.terricom.mytype.tools.FORMAT_HH_MM
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.tools.toDateFormat
import java.sql.Timestamp
import java.util.*

class DiaryViewModel: ViewModel() {

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
        updatePuzzle()
    }

    fun getAndSetFoodieShapeSleepToday() {

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {user ->

                user.collection(FirebaseKey.COLLECTION_SHAPE)
                    .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                    .whereLessThanOrEqualTo(TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_dayend,
                                date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                        )
                    )
                    .whereGreaterThanOrEqualTo(TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_daybegin,
                                date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                        )
                    )
                    .get()
                    .addOnSuccessListener {

                        val items = mutableListOf<Shape>()
                        for (document in it) {

                            val convertDate = java.sql.Date(document.toObject(Shape::class.java).timestamp!!.time)
                            if (convertDate.toString() == date.value.toDateFormat(FORMAT_YYYY_MM_DD)){

                                items.add(document.toObject(Shape::class.java))
                                items[items.lastIndex].docId = document.id
                            }
                        }
                        if (items.size != 0){

                            setDataShapeFromFIrebase(items[0])
                        }
                    }

                user.collection(FirebaseKey.COLLECTION_SLEEP)
                    .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                    .whereLessThanOrEqualTo(TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_dayend,
                                date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                        )
                    )
                    .whereGreaterThanOrEqualTo(TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_daybegin,
                                date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                        )
                    )
                    .get()
                    .addOnSuccessListener {

                        val items = mutableListOf<Sleep>()
                        for (document in it) {

                            val convertDate = java.sql.Date(document.toObject(Sleep::class.java).wakeUp!!.time)
                            if (convertDate.toString() == date.value.toDateFormat(FORMAT_YYYY_MM_DD)){

                                items.add(document.toObject(Sleep::class.java))
                                items[items.lastIndex].docId = document.id
                            }

                        }
                        if (items.size != 0){

                            setDataSleepFromFirebase(items[0])
                        }
                    }

                user.collection(COLLECTION_FOODIE)
                    .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                    .whereLessThanOrEqualTo(TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_dayend,
                                date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                        )
                    )
                    .whereGreaterThanOrEqualTo(TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_daybegin,
                                date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                        )
                    )
                    .get()
                    .addOnSuccessListener {

                        val items = mutableListOf<Foodie>()
                        val dates = mutableListOf<String>()
                        for (document in it) {

                            val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                            if (convertDate.toString() == date.value.toDateFormat(FORMAT_YYYY_MM_DD)){
                                items.add(document.toObject(Foodie::class.java))
                                items[items.lastIndex].docId = document.id
                                dates.add(document.toObject(Foodie::class.java).timestamp.toDateFormat(
                                    FORMAT_YYYY_MM_DD)
                                )
                            }
                        }

                        _totalWater.value = 0f
                        _totalOil.value = 0f
                        _totalVegetable.value = 0f
                        _totalProtein.value = 0f
                        _totalFruit.value = 0f
                        _totalCarbon.value = 0f

                        for (foo in items){

                            _totalWater.value = _totalWater.value!!.plus(foo.water ?: 0f)
                            _totalOil.value = _totalOil.value!!.plus(foo.oil ?: 0f)
                            _totalVegetable.value = _totalVegetable.value!!.plus(foo.vegetable ?: 0f)
                            _totalProtein.value = _totalProtein.value!!.plus(foo.protein ?: 0f)
                            _totalCarbon.value = _totalCarbon.value!!.plus(foo.carbon ?: 0f)
                            _totalFruit.value = _totalFruit.value!!.plus(foo.fruit ?:0f)
                        }
                        setDataFoodieFromFirebase(items)
                        _status.value = true
                    }
            }
        }
    }

    private val _totalWater = MutableLiveData<Float>()
    private val _totalOil = MutableLiveData<Float>()
    private val _totalVegetable = MutableLiveData<Float>()
    private val _totalProtein = MutableLiveData<Float>()
    private val _totalFruit = MutableLiveData<Float>()
    private val _totalCarbon = MutableLiveData<Float>()


    val totalWater: LiveData<Float>
        get() = _totalWater

    val totalOil: LiveData<Float>
        get() = _totalOil

    val totalVegetable: LiveData<Float>
        get() = _totalVegetable

    val totalProtein: LiveData<Float>
        get() = _totalProtein

    val totalFruit: LiveData<Float>
        get() = _totalFruit

    val totalCarbon: LiveData<Float>
        get() = _totalCarbon

    private val _listQueryFoodieResult = MutableLiveData<FoodieList>()
    val listQueryFoodieResult : LiveData<FoodieList>
        get() = _listQueryFoodieResult

    private fun setQueryResult(queryResult : List<Foodie>, key: String){
        _listQueryFoodieResult.value = FoodieList(queryResult,key)
    }

    fun getTime(timestamp: Date):String{
            return java.sql.Date(timestamp.time).toDateFormat(FORMAT_HH_MM)
    }

    fun delete(foodie: Foodie){
        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let { user ->

                user.collection(COLLECTION_FOODIE)
                    .orderBy(
                        TIMESTAMP,
                        Query.Direction.DESCENDING)
                    .whereLessThanOrEqualTo(
                        TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_dayend,
                                date.value.toDateFormat(FORMAT_YYYY_MM_DD)
                            )
                        )
                    )
                    .whereGreaterThanOrEqualTo(
                        TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_daybegin,
                                date.value.toDateFormat(FORMAT_YYYY_MM_DD)
                            )
                        )
                    )
                    .get()
                    .addOnSuccessListener {
                        for (diary in it){
                            if (diary.id == foodie.docId){

                                user.collection(COLLECTION_FOODIE).document(foodie.docId).delete()
                                    .addOnSuccessListener {
                                        callDeleteAction()
                                    }
                                    .addOnFailureListener {
                                            e -> Logger.i("Error deleting document exception = $e")
                                    }
                            }
                        }
                    }
            }

        }
    }

    private fun updatePuzzle() {

        if (UserManager.isLogin()){

            UserManager.USER_REFERENCE?.let {user ->

                user.collection(COLLECTION_FOODIE)
                    .orderBy(TIMESTAMP,
                        Query.Direction.DESCENDING
                    )
                    .get()
                    .addOnSuccessListener {
                        val dates = mutableListOf<String>()
                        val items = mutableListOf<Foodie>()
                        for (document in it) {

                            dates.add(java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time).toDateFormat(
                                FORMAT_YYYY_MM_DD
                            ))
                            items.add(document.toObject(Foodie::class.java))
                        }
                        if (dates.distinct().size % 7 == 0){

                            //全新使用者
                            user.collection(COLLECTION_PUZZLE)
                                .orderBy(TIMESTAMP,
                                    Query.Direction.DESCENDING
                                )
                                .get()
                                .addOnSuccessListener {

                                    val puzzleAll = mutableListOf<Puzzle>()
                                    for (document in it) {

                                        puzzleAll.add(document.toObject(Puzzle::class.java))
                                        puzzleAll[puzzleAll.lastIndex].docId = document.id
                                    }

                                    Logger.i("puzzleAll.size = ${puzzleAll.size}")
                                    when (dates.size){
                                        0 -> {
                                            if (UserManager.getPuzzleNewUser == "0"  && puzzleAll.size == 0){

                                                UserManager.getPuzzleNewUser = UserManager.getPuzzleNewUser.toString().toInt().plus(1).toString()

                                                user.collection(COLLECTION_PUZZLE).document().set(
                                                    hashMapOf(
                                                        COLUMN_PUZZLE_POSITION to listOf((0..14).random()),
                                                        COLUMN_PUZZLE_IMGURL to PuzzleImg.values()[0].value,
                                                        COLUMN_PUZZLE_RECORDEDDATES to listOf(Date().toDateFormat(
                                                            FORMAT_YYYY_MM_DD
                                                        )),
                                                        TIMESTAMP to FieldValue.serverTimestamp()
                                                    )
                                                )

                                                getPuzzleNewUser()
                                            } else if (UserManager.getPuzzleNewUser == "1"  && puzzleAll.size == 1){

                                                UserManager.getPuzzleNewUser = UserManager.getPuzzleNewUser.toString().toInt().plus(1).toString()
                                                getPuzzleNewUser()
                                            }
                                        }
                                        else -> {
                                            getPuzzleOldUser()
                                        }
                                    }
                                }
                        }
                    }

                }
            }
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