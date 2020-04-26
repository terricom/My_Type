package com.terricom.mytype.shaperecord

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.FirebaseKey
import com.terricom.mytype.data.Result
import com.terricom.mytype.data.Shape
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.FORMAT_YYYY_MM_DD
import com.terricom.mytype.tools.toDateFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*

class ShapeRecordViewModel(private val myTypeRepository: MyTypeRepository): ViewModel() {

    private val _date = MutableLiveData<Date>()
    val date: LiveData<Date>
        get() = _date

    fun setDate(date: Date){
        _date.value = date
    }

    var weight = MutableLiveData<String>()
    var bodyWater = MutableLiveData<String>()
    var bodyFat = MutableLiveData<String>()
    var muscle = MutableLiveData<String>()
    var tdee = MutableLiveData<String>()
    var bodyAge = MutableLiveData<String>()

    fun convertStringToFloat(string: String): Float {
        return try {
            string.toFloat()
        } catch (nfe: NumberFormatException) {
            0f
        }
    }

    private val _isAddDataShape = MutableLiveData<Boolean>()
    val isAddDataShape : LiveData<Boolean>
        get() = _isAddDataShape

    private fun addShapeSuccess(){
        _isAddDataShape.value = true
    }

    private fun addShapeFail(){
        _isAddDataShape.value = false
    }

    init {
        setDate(Date())
    }

    @InverseMethod("convertStringToFloat")
    fun floatToString(value:Float) = value.toString()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun addOrUpdateShape2Firebase(docId: String){

        val shapeContent = hashMapOf(
            FirebaseKey.TIMESTAMP to Timestamp(date.value!!.time),
            FirebaseKey.COLUMN_SHAPE_WEIGHT to (weight.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_SHAPE_BODY_WATER to (bodyWater.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_SHAPE_BODY_FAT to (bodyFat.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_SHAPE_MUSCLE to (muscle.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_SHAPE_TDEE to (tdee.value ?: "0.0").toFloat(),
            FirebaseKey.COLUMN_SHAPE_BODY_AGE to (bodyAge.value ?: "0.0").toFloat()
        )

        coroutineScope.launch {

            when(docId){
                "" -> {
                    when(cleanDates.contains(date.value.toDateFormat(FORMAT_YYYY_MM_DD))){

                        true -> addShapeFail()
                        false -> {
                            myTypeRepository.setOrUpdateObjects(FirebaseKey.COLLECTION_SHAPE, shapeContent, docId)
                            addShapeSuccess()
                        }
                    }
                }
                else -> {
                    myTypeRepository.setOrUpdateObjects(FirebaseKey.COLLECTION_SHAPE, shapeContent, docId)
                    addShapeSuccess()
                }
            }
        }
    }

    private val cleanDates = mutableListOf<String>()

    fun getRecordedDates() {

        coroutineScope.launch {

            val shapeResult = myTypeRepository.getObjects<Shape>(FirebaseKey.COLLECTION_SHAPE,
                Timestamp.valueOf(
                App.applicationContext().getString(
                    R.string.timestamp_daybegin,
                    date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                ),
                Timestamp.valueOf(
                    App.applicationContext().getString(
                        R.string.timestamp_dayend,
                        date.value.toDateFormat(FORMAT_YYYY_MM_DD))
                )
            )

            if (shapeResult is Result.Success) {
                shapeResult.data.forEach {
                    cleanDates.add(it.timestamp.toDateFormat(FORMAT_YYYY_MM_DD)) }
            }
        }
    }

    fun clearData(){
        weight.value = ""
        bodyAge.value = ""
        bodyFat.value = ""
        bodyWater.value = ""
        tdee.value = ""
        muscle.value = ""
    }
}