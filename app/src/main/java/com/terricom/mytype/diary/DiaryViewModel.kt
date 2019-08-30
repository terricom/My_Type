package com.terricom.mytype.diary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiaryViewModel: ViewModel() {

    private val dropItems = mutableListOf<String>()

    val addNutrition = MutableLiveData<String>()
    val adddNutrition: LiveData<String>
        get() = addNutrition

    fun dragToList(nutrition: String) {
        dropItems.add(nutrition)
        Log.i("Terri", "DiaryViewModel dropItems = $dropItems")
    }

}