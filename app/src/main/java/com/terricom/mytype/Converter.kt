package com.terricom.mytype

import androidx.databinding.InverseMethod
import java.text.SimpleDateFormat
import java.util.*

object Converter {
    private val dateFormat = SimpleDateFormat(App.applicationContext().getString(R.string.simpledateformat_yyyy_MM_dd), Locale.getDefault())
    private val hourFormat = SimpleDateFormat(App.applicationContext().getString(R.string.simpledateformat_HH_mm), Locale.getDefault())

    @JvmStatic
    @InverseMethod("stringToDate")
    fun dateToString(date: Date): String {
        return dateFormat.format(date)
    }

    @JvmStatic
    fun stringToDate(string: String): Date {
        return dateFormat.parse(string)
    }

    @JvmStatic
    @InverseMethod("stringToHour")
    fun hourToString(date: Date): String {
        return hourFormat.format(date)
    }

    @JvmStatic
    fun stringToHour(string: String): Date {
        return hourFormat.parse(string)
    }
}