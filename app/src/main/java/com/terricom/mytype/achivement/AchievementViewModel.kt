package com.terricom.mytype.achivement

import androidx.lifecycle.ViewModel
import java.sql.Timestamp
import java.text.SimpleDateFormat

class AchievementViewModel: ViewModel() {


    fun getTime(time: Timestamp): String{
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
        val format1 = simpleDateFormat.format(time)
        return format1
    }
}