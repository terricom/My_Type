package com.terricom.mytype.linechart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class CalendarLinechartViewModel: ViewModel() {

    private val _date = MutableLiveData<String>()

    val date: LiveData<String>
        get() = _date

    fun setDate(date: String){
        _date.value = date
    }




}