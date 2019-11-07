package com.terricom.mytype.achievement

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.linechart.ChartEntity
import com.terricom.mytype.tools.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class AchievementViewModel(private val myTypeRepository: MyTypeRepository): ViewModel() {

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

    val goal: LiveData<List<Goal>> = myTypeRepository.getGoal()

    private val _dateFormatMonth = MutableLiveData<String>()
    val dateFormatMonth: LiveData<String>
        get() = _dateFormatMonth

    private val _dataShapeFromFirebase = MutableLiveData<List<Shape>>()
    val dataShapeFromFirebase : LiveData<List<Shape>>
        get() = _dataShapeFromFirebase

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean>
        get() = _status

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

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        setCurrentDate(Date())
        getAndSetDataShapeOfThisMonth()
    }

    @SuppressLint("StringFormatMatches")
    fun getAndSetDataShapeOfThisMonth() {

        coroutineScope.launch {

            val shapeList = myTypeRepository.getObjects(FirebaseKey.COLLECTION_SHAPE,
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_daybegin,
                    "${currentDate.value.toDateFormat(FORMAT_YYYY_MM)}-01"
                )),
                Timestamp.valueOf(
                    App.applicationContext().getString(R.string.timestamp_dayend,
                        "${currentDate.value.toDateFormat(FORMAT_YYYY_MM)}-${getThisMonthLastDate()}"
                    )
                )
            )

            val dateList = mutableListOf<String>()
            for (shape in shapeList){

                dateList.add((shape as Shape).timestamp.toDateFormat(FORMAT_MM_DD))
            }
            setRecordedDatesOfThisMonth(ArrayList(dateList.distinct()))

            val weightList = mutableListOf<Float>()
            val bodyFatList = mutableListOf<Float>()
            val muscleList = mutableListOf<Float>()

            val weightListTemp = mutableListOf<Float>()
            val bodyFatListTemp = mutableListOf<Float>()
            val muscleListTemp = mutableListOf<Float>()
            val shapeListTemp = mutableListOf<Shape>()

            for (eachDay in dateList.distinct()){

                weightListTemp.clear()
                bodyFatListTemp.clear()
                muscleListTemp.clear()

                for (i in 0 until shapeList.size){

                    if ((shapeList[i] as Shape).timestamp?.toDateFormat(FORMAT_MM_DD) == eachDay){

                        (shapeList[i] as Shape).let {shape ->

                            shapeListTemp.add(shape)

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

            chartList.clear()

            chartList.add(ChartEntity(App.applicationContext()
                .getColor(R.color.colorPinky), weightList.toFloatArray()))
            chartList.add(ChartEntity(App.applicationContext()
                .getColor(R.color.colorButton), bodyFatList.toFloatArray()))
            chartList.add(ChartEntity(App.applicationContext()
                .getColor(R.color.blue_facebook), muscleList.toFloatArray()))

            setListDates(chartList.toCollection(ArrayList()))
            _status.value = true

            if (shapeListTemp.size != 0){

                setDataShapeFromFirebase(shapeListTemp.distinct())
            }
            _listOfChartEntities.value = null
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


