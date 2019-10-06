package com.terricom.mytype.achievement

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.terricom.mytype.*
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_SHAPE
import com.terricom.mytype.data.FirebaseKey.Companion.COLLECTION_USERS
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.linechart.ChartEntity
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class AchievementViewModel: ViewModel() {

    val userUid = UserManager.uid

    private val currentCalendar = Calendar.getInstance()

    val goalWeight = MutableLiveData<String>()
    val goalBodyFat = MutableLiveData<String>()
    val goalMuscle = MutableLiveData<String>()

    val diffWeight = MutableLiveData<String>()
    val diffBodyFat = MutableLiveData<String>()
    val diffMuscle = MutableLiveData<String>()

    val diffWeightNumber = MutableLiveData<Float>()
    val diffBodyFatNumber = MutableLiveData<Float>()
    val diffMuscleNumber = MutableLiveData<Float>()

    private val _dateFormatMonth = MutableLiveData<String>()
    val dateFormatMonth: LiveData<String>
        get() = _dateFormatMonth

    private val _dataShapeFromFirebase = MutableLiveData<List<Shape>>()
    val dataShapeFromFirebase : LiveData<List<Shape>>
        get() = _dataShapeFromFirebase

    private fun setDataShapeFromFirebase (shape: List<Shape>){
        _dataShapeFromFirebase.value = shape
    }

    private val _currentDate = MutableLiveData<Date>()
    val currentDate: LiveData<Date>
        get() = _currentDate

    fun setCurrentDate(date: Date){

        _dateFormatMonth.value = date.toDateFormat(FORMAT_YYYY_MM)
        _currentDate.value = date
    }

    private val _recordedDatesOfThisMonth = MutableLiveData<ArrayList<String>>()
    val recordedDatesOfThisMonth : LiveData<ArrayList<String>>
        get() = _recordedDatesOfThisMonth

    private fun setRecordedDatesOfThisMonth (recordedDateList: ArrayList<String>){

        _recordedDatesOfThisMonth.value = recordedDateList
    }

    private val _listOfChartEntities = MutableLiveData<ArrayList<ChartEntity>>()
    val listOfChartEntities : LiveData<ArrayList<ChartEntity>>
        get() = _listOfChartEntities

    private fun setListDates(chartEntitiesList: ArrayList<ChartEntity>){

        _listOfChartEntities.value = chartEntitiesList
    }

    init {
        setCurrentDate(Date())
    }

    @SuppressLint("StringFormatMatches")
    fun getAndSetDataShapeOfThisMonth() {

        if (UserManager.isLogin()){

            getGoal()

            UserManager.uid?.let { it ->

                FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                    .document(it).collection(COLLECTION_SHAPE)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.ASCENDING)
                    .whereLessThanOrEqualTo(

                        FirebaseKey.TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_dayend,

                                App.applicationContext().getString(R.string.year_month_date,
                                    "${currentCalendar.get(Calendar.YEAR)}",
                                    "${currentCalendar.get(Calendar.MONTH) + 1}",
                                    "${getThisMonthLastDate()}"
                                )
                            )
                        )
                    )
                    .whereGreaterThanOrEqualTo(

                        FirebaseKey.TIMESTAMP,
                        Timestamp.valueOf(
                            App.applicationContext().getString(R.string.timestamp_daybegin,
                                App.applicationContext().getString(R.string.year_month_date,
                                    "${currentCalendar.get(Calendar.YEAR)}",
                                    "${currentCalendar.get(Calendar.MONTH) + 1}",
                                    "01")
                            )
                        )
                    )
                    .get()
                    .addOnSuccessListener { it ->

                        val items = mutableListOf<Shape>()
                        val dateList = mutableListOf<String>()
                        val weightList = mutableListOf<Float>()
                        val bodyFatList = mutableListOf<Float>()
                        val muscleList = mutableListOf<Float>()

                        for (document in it){

                            items.add(document.toObject(Shape::class.java))
                            items[items.lastIndex].docId = document.id
                            dateList.add(document.toObject(Shape::class.java).timestamp.toDateFormat(
                                FORMAT_MM_DD))
                        }

                        setRecordedDatesOfThisMonth(ArrayList(dateList.distinct()))

                        // 字尾帶 Temp 的工具人是用來接住依照日期篩選後的數值
                        val weightListTemp = mutableListOf<Float>()
                        val bodyFatListTemp = mutableListOf<Float>()
                        val muscleListTemp = mutableListOf<Float>()
                        val shapeListTemp = mutableListOf<Shape>()

                        for (eachDay in dateList.distinct()){

                            weightListTemp.clear()
                            bodyFatListTemp.clear()
                            muscleListTemp.clear()

                            for (i in 0 until items.size){

                                if (items[i].timestamp?.toDateFormat(FORMAT_MM_DD) == eachDay){

                                    items[i].let {shape ->

                                        shapeListTemp.add(items[i])

                                        shape.weight?.let {
                                            weightListTemp.add(it)
                                        }
                                        shape.bodyFat?.let {
                                            bodyFatListTemp.add(it)
                                        }
                                        shape.muscle?.let {
                                            muscleListTemp.add(it)
                                        }
                                    }
                                }
                            }

                            // 篩選當天最新一筆紀錄加入日期清單
                            weightList.add(weightListTemp[0])
                            bodyFatList.add(bodyFatListTemp[0])
                            muscleList.add(muscleListTemp[0])

                        }

                        if (weightList.size > 0) {

                            diffWeight.value =
                                weightList[weightList.lastIndex]
                                    .minus(goalWeight.value.toFloatFormat()).toDemicalPoint(1)


                            diffWeightNumber.value =
                                weightList[weightList.lastIndex]
                                    .minus(goalWeight.value.toFloatFormat())
                        }

                        if (bodyFatList.size > 0) {

                            diffBodyFat.value =
                                bodyFatList[bodyFatList.lastIndex]
                                    .minus(goalBodyFat.value.toFloatFormat()).toDemicalPoint(1)


                            diffBodyFatNumber.value =
                                bodyFatList[bodyFatList.lastIndex]
                                    .minus(goalBodyFat.value.toFloatFormat())

                        }
                        if (muscleList.size >0) {

                            diffMuscle.value =
                                muscleList[muscleList.lastIndex]
                                    .minus(goalMuscle.value.toFloatFormat()).toDemicalPoint(1)


                            diffMuscleNumber.value =
                                muscleList[muscleList.lastIndex]
                                .minus(goalMuscle.value.toFloatFormat())
                        }



                        val chartList = mutableListOf<ChartEntity>()

                        chartList.add(ChartEntity(App.applicationContext()
                            .getColor(R.color.colorPinky), weightList.toFloatArray()))
                        chartList.add(ChartEntity(App.applicationContext()
                            .getColor(R.color.colorButton), bodyFatList.toFloatArray()))
                        chartList.add(ChartEntity(App.applicationContext()
                            .getColor(R.color.blue_facebook), muscleList.toFloatArray()))

                        setListDates(chartList.toCollection(ArrayList()))

                        if (shapeListTemp.size != 0){

                            setDataShapeFromFirebase(shapeListTemp)
                        }

                        // 更新完 ChartEntities 後清除 LiveData 資料
                        _listOfChartEntities.value = null

                    }

            }
        }
    }

    private fun getGoal() {

        if (UserManager.isLogin()){

            UserManager.uid?.let {it ->

                FirebaseFirestore.getInstance()
                    .collection(COLLECTION_USERS)
                    .document(it)
                    .collection(FirebaseKey.COLLECTION_GOAL)
                    .orderBy(FirebaseKey.TIMESTAMP, Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener {

                        val items = mutableListOf<Goal>()

                        for (document in it) {

                            items.add(document.toObject(Goal::class.java))
                            items[items.size-1].docId = document.id
                        }

                        if (items.size > 0){

                            goalWeight.value = items[0].weight.toDemicalPoint(1)
                            goalBodyFat.value = items[0].bodyFat.toDemicalPoint(1)
                            goalMuscle.value = items[0].muscle.toDemicalPoint(1)
                        }

                    }
            }

        }
    }

    private fun getThisMonthLastDate(): Int {

        currentCalendar.time = currentDate.value
        currentCalendar.add(Calendar.MONTH, 0)
        currentCalendar.set(
            Calendar.DAY_OF_MONTH, currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )

        return currentCalendar.get(Calendar.DAY_OF_MONTH)
    }

}


