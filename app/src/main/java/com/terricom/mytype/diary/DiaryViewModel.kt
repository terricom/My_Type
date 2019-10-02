package com.terricom.mytype.diary

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.*
import com.terricom.mytype.profile.CardAvatarOutlineProvider
import com.terricom.mytype.tools.Logger
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class DiaryViewModel: ViewModel() {

    val userUid = UserManager.uid

    val outlineProvider = CardAvatarOutlineProvider()

    private val _date = MutableLiveData<Date>()
    val date : LiveData<Date>
        get() = _date

    private val _fireFoodie = MutableLiveData<List<Foodie>>()
    val fireFoodie : LiveData<List<Foodie>>
        get() = _fireFoodie

    private fun fireFoodieBack (foo: List<Foodie>){
        _fireFoodie.value = foo
    }

    val _fireShape = MutableLiveData<Shape>()
    val fireShape : LiveData<Shape>
        get() = _fireShape

    fun fireShapeBack (shape: Shape){
        _fireShape.value = shape
    }

    private val _fireSleep = MutableLiveData<Sleep>()
    val fireSleep : LiveData<Sleep>
        get() = _fireSleep

    private fun fireSleepBack (sleep: Sleep){
        _fireSleep.value = sleep
    }

    fun setCurrentDate(date: Date){
        _date.value = date
    }

    private fun clearFireShape(){
        _fireShape.value = null
    }

    private fun clearFireSleep(){
        _fireSleep.value = null
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

    private fun getPuzzle(){
        _isGetPuzzle.value = true
    }

    private fun getPuzzleNewUser(){
        _isGetPuzzle.value = false
    }

    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat(App.applicationContext().getString(R.string.simpledateformat_yyyy_MM_dd))
    val sdf_hm = SimpleDateFormat(App.applicationContext().getString(R.string.simpledateformat_HH_mm))
    val sdf_ym = SimpleDateFormat(App.applicationContext().getString(R.string.simpledateformat_yyyy_MM))

    val db = FirebaseFirestore.getInstance()
    val users = db.collection(collectionUsers)

    private var storageRef : StorageReference?= null

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

    fun getDiary() {

        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid).collection(collectionFoodie)
                .orderBy(App.applicationContext().getString(R.string.timestamp), Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo(App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_dayend,
                            "${sdf.format(date.value)}")
                    )
                )
                .whereGreaterThanOrEqualTo(App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_daybegin,
                            "${sdf.format(date.value)}")
                    )
                )
            val shapeDiary = users
                .document(userUid).collection(collectionShape)
                .orderBy(App.applicationContext().getString(R.string.timestamp), Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo(App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_dayend,
                            "${sdf.format(date.value)}")
                    )
                )
                .whereGreaterThanOrEqualTo(App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_daybegin,
                            "${sdf.format(date.value)}")
                    )
                )
            val sleepDiary = users
                .document(userUid).collection(collectionSleep)
                .orderBy(App.applicationContext().getString(R.string.timestamp), Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo(App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_dayend,
                            "${sdf.format(date.value)}")
                    )
                )
                .whereGreaterThanOrEqualTo(App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_daybegin,
                            "${sdf.format(date.value)}")
                    )
                )

        shapeDiary
            .get()
            .addOnSuccessListener {

                val items = mutableListOf<Shape>()
                for (document in it) {

                    val convertDate = java.sql.Date(document.toObject(Shape::class.java).timestamp!!.time)
                    if (convertDate.toString() == sdf.format(date.value)){

                        items.add(document.toObject(Shape::class.java))
                        items[items.lastIndex].docId = document.id
                    }
                }
                if (items.size != 0){

                    fireShapeBack(items[0])
                } else {
                    clearFireShape()
                }
            }

        sleepDiary
            .get()
            .addOnSuccessListener {

                val items = mutableListOf<Sleep>()
                for (document in it) {

                    val convertDate = java.sql.Date(document.toObject(Sleep::class.java).wakeUp!!.time)
                    if (convertDate.toString() == sdf.format(date.value)){

                        items.add(document.toObject(Sleep::class.java))
                        items[items.lastIndex].docId = document.id
                    }

                }
                if (items.size != 0){

                    fireSleepBack(items[0])
                } else {
                    clearFireSleep()
                }
            }

        foodieDiary
            .get()
            .addOnSuccessListener {
                storageRef = FirebaseStorage.getInstance().reference

                val items = mutableListOf<Foodie>()
                for (document in it) {

                    val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                    if (convertDate.toString() == sdf.format(date.value)){
                        items.add(document.toObject(Foodie::class.java))
                        items[items.lastIndex].docId = document.id
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
                fireFoodieBack(items)
            }
        }

    }

    val _totalWater = MutableLiveData<Float>()
    val _totalOil = MutableLiveData<Float>()
    val _totalVegetable = MutableLiveData<Float>()
    val _totalProtein = MutableLiveData<Float>()
    val _totalFruit = MutableLiveData<Float>()
    val _totalCarbon = MutableLiveData<Float>()


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

    val _listQueryFoodieResult = MutableLiveData<FoodieList>()
    val listQueryFoodieResult : LiveData<FoodieList>
        get() = _listQueryFoodieResult

    fun setQueryResult(queryResult : List<Foodie>, key: String){
        _listQueryFoodieResult.value = FoodieList(queryResult,key)
    }

    fun getTime(timestamp: Date):String{
            return sdf_hm.format(java.sql.Date(timestamp.time).time)
    }

    fun getThisMonth() {
        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid).collection(collectionFoodie)
                .orderBy(App.applicationContext().getString(R.string.timestamp), Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo(
                    App.applicationContext().getString(R.string.timestamp),
                    App.applicationContext().getString(R.string.timestamp_dayend,
                        "${sdf_ym.format(date.value)}-31"
                    )
                )
                .whereGreaterThanOrEqualTo(
                    App.applicationContext().getString(R.string.timestamp),
                    App.applicationContext().getString(R.string.timestamp_daybegin,
                        "${sdf_ym.format(date.value)}-01"
                    )
                )


        foodieDiary
            .get()
            .addOnSuccessListener {

                val items = mutableListOf<Foodie>()
                for (document in it) {

                    items.add(document.toObject(Foodie::class.java))
                    items[items.lastIndex].docId = document.id
                }
            }
        }
    }


    fun delete(foodie: Foodie){
        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid).collection(collectionFoodie)
                .orderBy(
                    App.applicationContext().getString(R.string.timestamp),
                    Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo(
                    App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_dayend,
                            sdf.format(date.value)
                        )
                    )
                )
                .whereGreaterThanOrEqualTo(
                    App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_daybegin,
                            sdf.format(date.value)
                        )
                    )
                )

            foodieDiary
                .get()
                .addOnSuccessListener {
                    for (diary in it){
                        if (diary.id == foodie.docId){

                            users.document(userUid).collection(collectionFoodie).document(foodie.docId!!).delete()
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

    private fun updatePuzzle() {

        if (!userUid.isNullOrEmpty()){
            val diary = users
                .document(userUid).collection(collectionFoodie)
                .orderBy(App.applicationContext().getString(R.string.timestamp),
                    Query.Direction.DESCENDING
                )

            val puzzle = users
                .document(userUid).collection(collectionPuzzle)
                .orderBy(App.applicationContext().getString(R.string.timestamp),
                    Query.Direction.DESCENDING
                )

            diary
                .get()
                .addOnSuccessListener {
                    val dates = mutableListOf<String>()
                    val items = mutableListOf<Foodie>()
                    for (document in it) {

                        dates.add(sdf.format(java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)))
                        items.add(document.toObject(Foodie::class.java))
                    }
                    if (dates.distinct().size%7 == 0){

                        UserManager.createDiary = UserManager.createDiary.toString().toInt().plus(1).toString()
                        //全新使用者

                        puzzle
                            .get()
                            .addOnSuccessListener {
                                val puzzleAll = mutableListOf<Puzzle>()
                                for (document in it) {

                                    puzzleAll.add(document.toObject(Puzzle::class.java))
                                    puzzleAll[puzzleAll.lastIndex].docId = document.id
                                }

                                if (dates.size == 0 && UserManager.createDiary == "2" && puzzleAll.size == 0){
                                    val puzzle = hashMapOf(
                                        App.applicationContext().getString(R.string.puzzle_position) to listOf((0..14).random()),
                                        App.applicationContext().getString(R.string.puzzle_imgURL) to PuzzleImg.values()[0].value,
                                        App.applicationContext().getString(R.string.puzzle_recordedDates) to listOf(sdf.format(Date())),
                                        App.applicationContext().getString(R.string.puzzle_timestamp) to FieldValue.serverTimestamp()

                                    )
                                    users.document(userUid).collection(collectionPuzzle).document().set(puzzle)
                                    getPuzzleNewUser()
                                }
                                //老用戶
                                else if (dates.size != 0 ){
                                    getPuzzle()
                                }
                            }
                    }
                }
        }


    }

    fun queryFoodieFoods(key: String) {

        if (!userUid.isNullOrEmpty()){
            val diary = users
                .document(userUid).collection(collectionFoodie)
                .whereArrayContains(App.applicationContext().getString(R.string.foodie_foods), key)

            diary
                .get()
                .addOnSuccessListener {

                    val dates = mutableListOf<String>()
                    val items = mutableListOf<Foodie>()
                    for (document in it) {

                        dates.add(sdf.format(java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)))
                        items.add(document.toObject(Foodie::class.java))
                        items[items.lastIndex].docId = document.id
                    }
                    if (!items.isNullOrEmpty()){

                        setQueryResult(items.asReversed(), key)
                    }
                }
        }


    }

    fun queryFoodieNutritions(key: String) {

        if (userUid!!.isNotEmpty()){
            val diary = users
                .document(userUid).collection(collectionFoodie)
                .whereArrayContains(App.applicationContext().getString(R.string.foodie_nutritions), key)

            diary
                .get()
                .addOnSuccessListener {

                    val dates = mutableListOf<String>()
                    val items = mutableListOf<Foodie>()
                    for (document in it) {

                        dates.add(sdf.format(java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)))
                        items.add(document.toObject(Foodie::class.java))
                        items[items.lastIndex].docId = document.id
                    }

                    if (!items.isNullOrEmpty()){

                        setQueryResult(items.asReversed(), key)
                    }
                }
        }


    }

    companion object {

        const val collectionUsers: String = "Users"
        const val collectionShape: String = "Shape"
        const val collectionGoal: String = "Goal"
        const val collectionSleep: String = "Sleep"
        const val collectionFoodie: String = "Foodie"
        const val collectionPuzzle: String = "Puzzle"
    }


}