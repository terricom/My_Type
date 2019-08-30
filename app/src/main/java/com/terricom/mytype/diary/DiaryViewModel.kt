package com.terricom.mytype.diary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiaryViewModel: ViewModel() {

    private val _dragToList = MutableLiveData<String>()

    val dragToList: LiveData<String>
        get() = _dragToList

    val addNutrition = MutableLiveData<String>()
    val adddNutrition: LiveData<String>
        get() = addNutrition

    fun dragToList(nutrition: String) {
        _dragToList.value = nutrition
    }

}