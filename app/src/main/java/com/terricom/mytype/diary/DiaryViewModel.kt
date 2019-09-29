package com.terricom.mytype.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.terricom.mytype.data.*
import com.terricom.mytype.profile.CardAvatarOutlineProvider
import com.terricom.mytype.tools.Logger
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class DiaryViewModel: ViewModel() {

    val userUid = UserManager.uid

    val outlineProvider = CardAvatarOutlineProvider()

    val _date = MutableLiveData<Date>()
    val date : LiveData<Date>
        get() = _date

    val _fireFoodie = MutableLiveData<List<Foodie>>()
    val fireFoodie : LiveData<List<Foodie>>
        get() = _fireFoodie

    fun fireFoodieBack (foo: List<Foodie>){
        _fireFoodie.value = foo
    }

    val _fireFoodieM = MutableLiveData<List<Foodie>>()
    val fireFoodieM : LiveData<List<Foodie>>
        get() = _fireFoodieM

    fun fireFoodieBackM (foo: List<Foodie>){
        _fireFoodieM.value = foo
    }

    val _fireShape = MutableLiveData<Shape>()
    val fireShape : LiveData<Shape>
        get() = _fireShape

    fun fireShapeBack (shape: Shape){
        _fireShape.value = shape
    }

    val _fireSleep = MutableLiveData<Sleep>()
    val fireSleep : LiveData<Sleep>
        get() = _fireSleep

    fun fireSleepBack (sleep: Sleep){
        _fireSleep.value = sleep
    }

    fun filterdate(dato: Date){
        Logger.i("DiaryViewModel filterdate = ${dato}")
        _date.value = dato
    }

    fun clearFireShape(){
        _fireShape.value = null
    }

    fun clearFireSleep(){
        _fireSleep.value = null
    }

    val _calendarClicked = MutableLiveData<Boolean>()
    val calendarClicked : LiveData<Boolean>
        get() = _calendarClicked

    fun calendarClicked(){
        _calendarClicked.value = true
    }

    fun calendarClickedAgain(){
        _calendarClicked.value = false
    }

    val _recordedDate = MutableLiveData<List<String>>()
    val recordedDate : LiveData<List<String>>
        get() = _recordedDate

    fun setRecordedDate(recordedDate: List<String>){
        _recordedDate.value = recordedDate
    }

    val _getPuzzle = MutableLiveData<Boolean>()
    val getPuzzle : LiveData<Boolean>
        get() = _getPuzzle

    fun getPuzzle(){
        _getPuzzle.value = true
    }

    fun getPuzzleNewUser(){
        _getPuzzle.value = false
    }


    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfhms = SimpleDateFormat("yyyy-MM-dd-hhmmss")
    val currentDate = sdf.format(Date())

    val db = FirebaseFirestore.getInstance()
    val users = db.collection("Users")

    private var storageRef : StorageReference?= null

    val _callDeleteAction = MutableLiveData<Boolean>()
    val callDeleteAction : LiveData<Boolean>
        get() = _callDeleteAction

    fun callDeleteAction(){
        _callDeleteAction.value = true
    }

    fun finishCallDeleteAction(){
        _callDeleteAction.value = false
    }


    init {
        calendarClickedAgain()
        finishCallDeleteAction()
        updatePuzzle()
    }

    fun getDiary() {

        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid).collection("Foodie")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 23:59:59.000000000"))
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 00:00:00.000000000"))
            val shapeDiary = users
                .document(userUid).collection("Shape")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 23:59:59.000000000"))
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 00:00:00.000000000"))
            val sleepDiary = users
                .document(userUid).collection("Sleep")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 23:59:59.000000000"))
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 00:00:00.000000000"))

        shapeDiary
            .get()
            .addOnSuccessListener {
                val items = mutableListOf<Shape>()
                items.clear()
                for (document in it) {
                    val convertDate = java.sql.Date(document.toObject(Shape::class.java).timestamp!!.time)
                    if (convertDate.toString() == sdf.format(date.value)){
                        items.add(document.toObject(Shape::class.java))
                        val index = items.size -1
                        items[index].docId = document.id
                    }
                }
                if (items.size != 0){
                Logger.i("DiaryViewModel items fireShapeBack (items[0]) = ${items[0]}")
                fireShapeBack(items[0])
                } else {clearFireShape()}
            }

        sleepDiary
            .get()
            .addOnSuccessListener {
                val items = mutableListOf<Sleep>()
                items.clear()
                for (document in it) {
                    val convertDate = java.sql.Date(document.toObject(Sleep::class.java).wakeUp!!.time)
                    if (convertDate.toString() == sdf.format(date.value)){
                        items.add(document.toObject(Sleep::class.java))
                        val index = items.size -1
                        items[index].docId = document.id
                    }

                }
                if (items.size != 0){
                    Logger.i("DiaryViewModel items fireSleepBack (items[0]) = ${items[0]}")
                    fireSleepBack(items[0])
                } else {clearFireSleep()}

            }

        foodieDiary
            .get()
            .addOnSuccessListener {
                storageRef = FirebaseStorage.getInstance().reference

                val items = mutableListOf<Foodie>()
//                items.clear()
                for (document in it) {
                    val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                    if (convertDate.toString() == sdf.format(date.value)){
                        items.add(document.toObject(Foodie::class.java))
                        val index = items.size -1
                        items[index].docId = document.id
                    }
                }
                Logger.i("items.size == 0 or ${items.size}")
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
                Logger.i("fireFoodie =${fireFoodie.value}")


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

    val _queryResult = MutableLiveData<List<Foodie>>()
    val queryResult : LiveData<List<Foodie>>
        get() = _queryResult

    val _listFoodie = MutableLiveData<FoodieList>()
    val listFoodie : LiveData<FoodieList>
        get() = _listFoodie

    fun setQuery(queryResult : List<Foodie>, key: String){
        _queryResult.value = queryResult
        _listFoodie.value = FoodieList(queryResult,key)
    }

    fun getTime(timestamp: Date):String{
        val sdf = SimpleDateFormat("HH:mm")
            return sdf.format(java.sql.Date(timestamp.time).time)
    }

    fun getTimeWithoutZone(timestamp: Date):String{
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(java.sql.Date(timestamp.time).time)
    }

    fun getThisMonth() {
        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid).collection("Foodie")
                .orderBy("timestamp", Query.Direction.DESCENDING)
        foodieDiary
            .get()
            .addOnSuccessListener {
                val items = mutableListOf<Foodie>()
                for (document in it) {
                    val convertDate = java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)
                    if (date.value != null && "${sdf.format(convertDate).split("-")[0]}-" +
                        "${sdf.format(convertDate).split("-")[1]}" ==
                        "${sdf.format(date.value)!!.split("-")[0]}-" +
                        "${sdf.format(date.value)!!.split("-")[1]}"){
                        items.add(document.toObject(Foodie::class.java))
                        items[items.size-1].docId = document.id
                    }
                }
                if (items.size != 0) {
                }
                fireFoodieBackM(items)
                Logger.i("fireFoodieM =${fireFoodieM.value}")
            }
        }
    }


    fun delete(foodie: Foodie){
        if (userUid!!.isNotEmpty()){
            val foodieDiary = users
                .document(userUid).collection("Foodie")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 23:59:59.000000000"))
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf("${sdf.format(date.value)} 00:00:00.000000000"))

            foodieDiary
                .get()
                .addOnSuccessListener {
                    for (diary in it){
                        if (diary.id == foodie.docId){
                            users.document(userUid).collection("Foodie").document(foodie.docId!!).delete()
                                .addOnSuccessListener { Logger.i("${foodie.docId} DocumentSnapshot successfully deleted!")
                                callDeleteAction()
                                }
                                .addOnFailureListener { e -> Logger.i("Error deleting document exception = $e") }
                        }
                    }
                }



        }
    }

    fun updatePuzzle() {

        if (userUid!!.isNotEmpty()){
            val diary = users
                .document(userUid).collection("Foodie")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            diary
                .get()
                .addOnSuccessListener {
                    val dates = mutableListOf<String>()
                    val items = mutableListOf<Foodie>()
                    for (document in it) {
                        dates.add(sdf.format(java.sql.Date(document.toObject(Foodie::class.java).timestamp!!.time)))
                        items.add(document.toObject(Foodie::class.java))
                    }
                    Logger.i("dates.size = ${dates.distinct().size} dates = $dates")
                    if (dates.distinct().size%7 == 0){
                        UserManager.createDiary = UserManager.createDiary.toString().toInt().plus(1).toString()
                        //全新使用者
                        if (dates.size == 0 && UserManager.createDiary == "2"){
                            val pazzleOld = hashMapOf(
                                "position" to listOf((0..14).random()),
                                "imgURL" to PuzzleImg.values()[0].value,
                                "recordedDates" to listOf(sdf.format(Date())),
                                "timestamp" to FieldValue.serverTimestamp()

                            )
                            users.document(userUid).collection("Puzzle").document().set(pazzleOld)
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

    fun queryFoodie(key: String) {

        if (userUid!!.isNotEmpty()){
            val diary = users
                .document(userUid).collection("Foodie")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereArrayContains("foods", key)

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
                    Logger.i("dates.size with $key = ${dates.distinct().size} dates = $dates")
                    Logger.i("$key items = ${items}")
                    if (!items.isNullOrEmpty()){
                        setQuery(items.asReversed(), key)
                    }

                }
        }


    }

    fun queryFoodieNu(key: String) {

        if (userUid!!.isNotEmpty()){
            val diary = users
                .document(userUid).collection("Foodie")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereArrayContains("nutritions", key)

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
                    Logger.i("dates.size with $key = ${dates.distinct().size} dates = $dates")
                    Logger.i("$key items = ${items}")
                    if (!items.isNullOrEmpty()){
                        setQuery(items.asReversed(), key)
                    }
                }
        }


    }








}