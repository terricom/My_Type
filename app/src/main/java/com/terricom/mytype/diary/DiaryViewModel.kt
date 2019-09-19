package com.terricom.mytype.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.terricom.mytype.Logger
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.Sleep
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.profile.CardAvatarOutlineProvider
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
        _callDeleteAction.value = false
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

    fun getTime(timestamp: Date):String{
        val sdf = SimpleDateFormat("HH:mm a")
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
                                .addOnSuccessListener { Logger.i("${foodie.docId} DocumentSnapshot successfully deleted!") }
                                .addOnFailureListener { e -> Logger.i("Error deleting document exception = $e") }
                        }
                    }
                }



        }
    }






}