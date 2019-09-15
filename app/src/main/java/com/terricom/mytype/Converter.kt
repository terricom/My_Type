package com.terricom.mytype

import androidx.databinding.InverseMethod
import java.text.SimpleDateFormat
import java.util.*

object Converter {
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    private val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    //Without this annotation, the Kotlin compiler would generate the static method that is required in the layout
    //More info: https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#static-methods
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