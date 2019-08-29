package com.terricom.mytype.shaperecord

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShapeRecordViewModel: ViewModel() {

    private val _date = MutableLiveData<String>()

    val date: LiveData<String>
        get() = _date

    fun setDate(date: String){
        _date.value = date
    }

    var weight = MutableLiveData<String>()
    var bodyWater = MutableLiveData<String>()
    var bodyFat = MutableLiveData<String>()
    var muscle = MutableLiveData<String>()
    var tdee = MutableLiveData<String>()
    var bodyAge = MutableLiveData<String>()
}