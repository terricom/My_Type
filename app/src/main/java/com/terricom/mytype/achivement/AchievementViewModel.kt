package com.terricom.mytype.achivement

import android.annotation.SuppressLint
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
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AchievementViewModel: ViewModel() {

    val userUid = UserManager.uid
    @SuppressLint("SimpleDateFormat")
    val sdfM = SimpleDateFormat(App.applicationContext().getString(R.string.simpledateformat_MM_dd))

    private val currentCalendar = Calendar.getInstance()

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

    private val _fireShape = MutableLiveData<List<Shape>>()
    val fireShape : LiveData<List<Shape>>
        get() = _fireShape

    private fun fireShapeBack (shape: List<Shape>){
        _fireShape.value = shape
    }

    private val _recordDate = MutableLiveData<Date>()
    val recordDate: LiveData<Date>
        get() = _recordDate

    @SuppressLint("SimpleDateFormat")
    fun setDate(date: Date){

        _date.value = SimpleDateFormat(App.applicationContext()
            .getString(R.string.simpledateformat_yyyy_MM_dd)).format(date)

        _dateM.value = SimpleDateFormat(App.applicationContext()
            .getString(R.string.simpledateformat_yyyy_MM)).format(date)

        _recordDate.value = date
    }

    private val _fireDate = MutableLiveData<ArrayList<String>>()
    val fireDate : LiveData<ArrayList<String>>
        get() = _fireDate

    private fun fireDateBack (foo: ArrayList<String>){

        _fireDate.value = foo
    }

    val db = FirebaseFirestore.getInstance()
    val user = db.collection(collectionUsers)

    private val _listDates = MutableLiveData<ArrayList<ChartEntity>>()
    val listDates : LiveData<ArrayList<ChartEntity>>
        get() = _listDates

    private fun setListDates(listDates: ArrayList<ChartEntity>){

        _listDates.value = listDates
    }

    init {
        setDate(Date())
    }

    @SuppressLint("StringFormatMatches")
    fun getThisMonth() {

        if (userUid!!.isNotEmpty()){

            getGoal()

            val shapeDiary = user
                .document(userUid).collection(collectionShape)
                .orderBy(App.applicationContext().getString(R.string.timestamp), Query.Direction.ASCENDING)
                .whereLessThanOrEqualTo(

                    App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_dayend,
                    "${currentCalendar.get(Calendar.YEAR)}" +
                            "-${currentCalendar.get(Calendar.MONTH) + 1}" +
                            "-${getLastMonthLastDate()}")
                    )
                )
                .whereGreaterThanOrEqualTo(

                    App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_daybegin,
                        "${currentCalendar.get(Calendar.YEAR)}" +
                                "-${currentCalendar.get(Calendar.MONTH) + 1}-01")
                    )
                )

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

                    for (document in it){

                        items.add(document.toObject(Shape::class.java))
                        items[items.size-1].docId = document.id
                        datelist.add(sdfM.format(document.toObject(Shape::class.java).timestamp))
                    }

                    val cleanList = datelist.distinct()
                    chartList.clear()

                    for (eachDay in cleanList){

                        weightListD.clear()
                        bodyFatListD.clear()
                        muscleListD.clear()

                        for (i in 0 until items.size){

                            if (sdfM.format(items[i].timestamp?.time) == eachDay){

                                items[i].let {
                                    shapeListD.add(items[i])
                                    it.weight?.let {
                                        weightListD.add(it)
                                    }
                                    it.bodyFat?.let {
                                        bodyFatListD.add(it)
                                    }
                                    it.muscle?.let {
                                        muscleListD.add(it)
                                    }
                                }
                            }
                        }

                        weightList.add(weightListD[0])
                        bodyFatList.add(bodyFatListD[0])
                        muscleList.add(muscleListD[0])

                    }

                    if (weightList.size >0) {

                        diffWeight.value = App.applicationContext().getString(R.string.float_round_one)
                            .format(weightList[weightList.lastIndex].minus(
                            (if (goalWeight.value == "null" || goalWeight.value.isNullOrEmpty())"0"
                            else goalWeight.value)!!.toFloat()))

                        diffWeightNum.value = weightList[weightList.lastIndex].minus(
                            (if (goalWeight.value == "null" || goalWeight.value.isNullOrEmpty())"0"
                            else goalWeight.value)!!.toFloat())
                    }
                    if (bodyFatList.size >0) {

                        diffBodyFat.value = App.applicationContext().getString(R.string.float_round_one)
                            .format(bodyFatList[bodyFatList.lastIndex].minus(
                            (if (goalBodyFat.value == "null" || goalBodyFat.value.isNullOrEmpty())"0"
                            else goalBodyFat.value)!!.toFloat()))

                        diffBodyFatNum.value = bodyFatList[bodyFatList.lastIndex].minus(
                            (if (goalBodyFat.value == "null" || goalBodyFat.value.isNullOrEmpty())"0"
                            else goalBodyFat.value)!!.toFloat())
                    }
                    if (muscleList.size >0) {

                        diffMuscle.value = App.applicationContext().getString(R.string.float_round_one)
                            .format(muscleList[muscleList.lastIndex].minus(
                            (if (goalMuscle.value == "null" || goalMuscle.value.isNullOrEmpty())"0"
                            else goalMuscle.value)!!.toFloat()))

                        diffMuscleNum.value = muscleList[muscleList.lastIndex].minus(
                            (if (goalMuscle.value == "null" || goalMuscle.value.isNullOrEmpty())"0"
                            else goalMuscle.value)!!.toFloat())
                    }

                    fireDateBack(ArrayList(cleanList))

                    chartList.add(ChartEntity(App.applicationContext()
                        .getColor(R.color.colorPinky), weightList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext()
                        .getColor(R.color.colorButton), bodyFatList.toFloatArray()))
                    chartList.add(ChartEntity(App.applicationContext()
                        .getColor(R.color.blue_facebook), muscleList.toFloatArray()))

                    setListDates(chartList.toCollection(ArrayList()))

                    if (shapeListD.size != 0){

                        fireShapeBack(shapeListD)
                    }

                    _listDates.value = null

                }

        }
    }

    private fun getGoal() {

        val db = FirebaseFirestore.getInstance()
        val users = db.collection(collectionUsers)

        if (userUid!!.isNotEmpty()){

            val goal = users
                .document(userUid)
                .collection(collectionGoal)
                .orderBy(App.applicationContext().getString(R.string.timestamp), Query.Direction.DESCENDING)
                .whereLessThanOrEqualTo(
                    App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_dayend,
                            "${currentCalendar.get(Calendar.YEAR)}" +
                                    "-${currentCalendar.get(Calendar.MONTH) + 1}" +
                                    "-${getLastMonthLastDate()}")
                    )
                )
                .whereGreaterThanOrEqualTo(
                    App.applicationContext().getString(R.string.timestamp),
                    Timestamp.valueOf(
                        App.applicationContext().getString(R.string.timestamp_daybegin,
                            "${currentCalendar.get(Calendar.YEAR)}" +
                                    "-${currentCalendar.get(Calendar.MONTH) + 1}-01")
                    )
                )

            goal
                .get()
                .addOnSuccessListener {

                    val items = mutableListOf<Goal>()

                    if (!it.isEmpty){

                        for (document in it) {

                            items.add(document.toObject(Goal::class.java))
                            items[items.size-1].docId = document.id
                        }

                        goalWeight.value = App.applicationContext().getString(R.string.float_round_one)
                            .format(items[0].weight)
                        goalBodyFat.value = App.applicationContext().getString(R.string.float_round_one)
                            .format(items[0].bodyFat)
                        goalMuscle.value = App.applicationContext().getString(R.string.float_round_one)
                            .format(items[0].muscle)
                    }
                }
        }
    }

    private fun getLastMonthLastDate(): Int {

        currentCalendar.time = recordDate.value
        currentCalendar.add(Calendar.MONTH, 0)
        currentCalendar.set(
            Calendar.DAY_OF_MONTH, currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )

        return currentCalendar.get(Calendar.DAY_OF_MONTH)
    }

    companion object {

        val collectionUsers: String = "Users"
        val collectionShape: String = "Shape"
        val collectionGoal: String = "Goal"
        val collectionSleep: String = "Sleep"
        val collectionFoodie: String = "Foodie"
        val collectionPuzzle: String = "Puzzle"
    }

}