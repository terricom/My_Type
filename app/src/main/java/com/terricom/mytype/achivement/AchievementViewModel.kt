package com.terricom.mytype.achivement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.linechart.ChartEntity
import com.terricom.mytype.tools.Logger
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AchievementViewModel: ViewModel() {


    val userUid = UserManager.uid
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val sdfM = SimpleDateFormat("MM-dd")

    val currentCalendar = Calendar.getInstance()

    val goalWeight = MutableLiveData<String>()
    val goalBodyFat = MutableLiveData<String>()
    val goalMuscle = MutableLiveData<String>()

    val diffWeight = MutableLiveData<String>()
    val diffBodyFat = MutableLiveData<String>()
    val diffMuscle = MutableLiveData<String>()

    val diffWeightNum = MutableLiveData<Float>()
    val diffBodyFatNum = MutableLiveData<Float>()
    val diffMuscleNum = MutableLiveData<Float>()

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    private val _dateM = MutableLiveData<String>()
    val dateM: LiveData<String>
        get() = _dateM

    val _fireShape = MutableLiveData<List<Shape>>()
    val fireShape : LiveData<List<Shape>>
        get() = _fireShape

    fun fireShapeBack (shape: List<Shape>){
        _fireShape.value = shape
    }

    private val _recordDate = MutableLiveData<Date>()
    val recordDate: LiveData<Date>
        get() = _recordDate

    fun setDate(date: Date){//date format should be java.util.Date
        _date.value = sdf.format(date)
        _dateM.value = SimpleDateFormat("yyyy-MM").format(date)
        _recordDate.value = date
        Logger.i("viewModel.date.observe = ${dateM.value}")

    }
    val _fireDate = MutableLiveData<ArrayList<String>>()
    val fireDate : LiveData<ArrayList<String>>
        get() = _fireDate

    fun fireDateBack (foo: ArrayList<String>){
        _fireDate.value = foo
    }


    val db = FirebaseFirestore.getInstance()
    val user = db.collection("Users")

    val _userFoodList = MutableLiveData<List<String>>()
    val userFoodList : LiveData<List<String>>
        get() = _userFoodList

    val _listDates = MutableLiveData<ArrayList<ChartEntity>>()
    val listDates : LiveData<ArrayList<ChartEntity>>
        get() = _listDates

    fun setListDates(listDates: ArrayList<ChartEntity>){
        _listDates.value = listDates
    }

    init {
//        getShapeList()
        setDate(Date())
    }

    fun getShapeList(){

        if (userUid != null){
            val shapeDiary = user
                .document(userUid).collection("Shape")
                .orderBy("timestamp", Query.Direction.DESCENDING)

        shapeDiary.get()
            .addOnSuccessListener { result->
                val items = mutableListOf<Shape>()
                for (document in result) {
                        items.add(document.toObject(Shape::class.java))
                }

                if (items.size != 0){
                    fireShapeBack(items)
                }


            }
        }


    }

    fun getThisMonth() {
        if (userUid!!.isNotEmpty()){

            getGoal()

            val shapeDiary = user
                .document(userUid).collection("Shape")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf(
                    "${currentCalendar.get(Calendar.YEAR)}-${currentCalendar.get(Calendar.MONTH)+1}-" +
                            "${getLastMonthLastDate()} 23:59:59.000000000"
                ))
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf(
                    "${currentCalendar.get(Calendar.YEAR)}-${currentCalendar.get(Calendar.MONTH)+1}-" +
                            "01 00:00:00.000000000"
                ))




            val chartList = mutableListOf<ChartEntity>()

            shapeDiary
                .get()
                .addOnSuccessListener {
                    val items = mutableListOf<Shape>()
                    val datelist = mutableListOf<String>()
                    val weightList = mutableListOf<Float>()
                    val bodyFatList = mutableListOf<Float>()
                    val muscleList = mutableListOf<Float>()
                    val weightListD = mutableListOf<Float>()
                    val bodyFatListD = mutableListOf<Float>()
                    val muscleListD = mutableListOf<Float>()
                    val shapeListD = mutableListOf<Shape>()
                    val shapeList = mutableListOf<Shape>()

                    for (document in it){
                        val convertDate = java.sql.Date(document.toObject(Shape::class.java).timestamp!!.time)
                        if (date.value != null && "${sdf.format(convertDate).split("-")[0]}-" +
                            "${sdf.format(convertDate).split("-")[1]}" ==
                            "${date.value!!.split("-")[0]}-${date.value!!.split("-")[1]}"){
                            items.add(document.toObject(Shape::class.java))
                            items[items.size-1].docId = document.id
                            datelist.add(sdfM.format(document.toObject(Shape::class.java).timestamp))
                        }
                    }
                    Logger.i("datelist = $datelist items = $$items")
                    val cleanList = datelist.distinct()
                    chartList.clear()
                    for (eachDay in cleanList){
                        weightListD.clear()
                        bodyFatListD.clear()
                        muscleListD.clear()
                        for (i in 0 until items.size){
                            if (sdfM.format(items[i].timestamp?.time) == eachDay){
                                items[i]?.let {
                                    shapeListD.add(items[i])
                                }
                                items[i].weight?.let {
                                    weightListD.add(it)
                                }
                                items[i].bodyFat?.let {
                                    bodyFatListD.add(it)
                                }
                                items[i].muscle?.let {
                                    muscleListD.add(it)
                                }
                            }
                        }
                        weightList.add(weightListD[0])
                        bodyFatList.add(bodyFatListD[0])
                        muscleList.add(muscleListD[0])

                    }

                    if (weightList.size >0) {
                        diffWeight.value = "%.1f".format(weightList[weightList.lastIndex].minus(
                            (if (goalWeight.value == "null" || goalWeight.value.isNullOrEmpty())"0" else goalWeight.value)!!.toFloat()))
                        diffWeightNum.value = weightList[weightList.lastIndex].minus(
                            (if (goalWeight.value == "null" || goalWeight.value.isNullOrEmpty())"0" else goalWeight.value)!!.toFloat())
                    }
                    if (bodyFatList.size >0) {
                        diffBodyFat.value = "%.1f".format(bodyFatList[bodyFatList.lastIndex].minus(
                            (if (goalBodyFat.value == "null" || goalBodyFat.value.isNullOrEmpty())"0" else goalBodyFat.value)!!.toFloat()))
                        diffBodyFatNum.value = bodyFatList[bodyFatList.lastIndex].minus(
                            (if (goalBodyFat.value == "null" || goalBodyFat.value.isNullOrEmpty())"0" else goalBodyFat.value)!!.toFloat())
                    }
                    if (muscleList.size >0) {
                        diffMuscle.value = "%.1f".format(muscleList[muscleList.lastIndex].minus(
                            (if (goalMuscle.value == "null" || goalMuscle.value.isNullOrEmpty())"0" else goalMuscle.value)!!.toFloat()))
                        diffMuscleNum.value = muscleList[muscleList.lastIndex].minus(
                            (if (goalMuscle.value == "null" || goalMuscle.value.isNullOrEmpty())"0" else goalMuscle.value)!!.toFloat())
                    }

                    fireDateBack(ArrayList(cleanList))
                    Logger.i("shapeList = $shapeList")

                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorPinky), weightList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.colorButton), bodyFatList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext().getColor(R.color.blue_facebook), muscleList.toFloatArray()))
                    setListDates(chartList.toCollection(ArrayList()))
                    if (shapeListD.size != 0){
                        fireShapeBack(shapeListD)
                    }
                    _listDates.value = null
                    Logger.i("LinechartViewModel fireDate = ${fireDate.value} cleanList = $cleanList")

                }

        }
    }

    fun getGoal() {
        Logger.i("userUID = $userUid")
        val db = FirebaseFirestore.getInstance()
        val users = db.collection("Users")

        if (userUid!!.isNotEmpty()){
            val goal = users
                .document(userUid)
                .collection("Goal")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo("timestamp", Timestamp.valueOf(
                    "${currentCalendar.get(Calendar.YEAR)}-${currentCalendar.get(Calendar.MONTH)+1}-" +
                            "${getLastMonthLastDate()} 23:59:59.000000000"
                ))
                .whereGreaterThanOrEqualTo("timestamp", Timestamp.valueOf(
                    "${currentCalendar.get(Calendar.YEAR)}-${currentCalendar.get(Calendar.MONTH)+1}-" +
                            "01 00:00:00.000000000"
                ))

            goal
                .get()
                .addOnSuccessListener {
                    val items = mutableListOf<Goal>()
                    if (it.isEmpty){

                    }else {
                        for (document in it) {
                            items.add(document.toObject(Goal::class.java))
                            items[items.size-1].docId = document.id
                        }
                        goalWeight.value = "%.1f".format(items[0].weight)
                        goalBodyFat.value = "%.1f".format(items[0].bodyFat)
                        goalMuscle.value = "%.1f".format(items[0].muscle)
                    }
                }
        }
    }

    private fun getLastMonthLastDate(): Int {
        currentCalendar.time = recordDate.value

        currentCalendar.add(Calendar.MONTH, 0)
        val max = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        currentCalendar.set(Calendar.DAY_OF_MONTH, max)

        return currentCalendar.get(Calendar.DAY_OF_MONTH)
    }


}