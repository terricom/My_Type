package com.terricom.mytype

import java.text.SimpleDateFormat
import java.util.*

fun Date?.toDateFormat(dateFormat: Int): String {

    return SimpleDateFormat(
        when (dateFormat){
            FORMAT_MM_DD -> App.applicationContext().getString(R.string.simpledateformat_MM_dd)
            FORMAT_YYYY_MM -> App.applicationContext().getString(R.string.simpledateformat_yyyy_MM)
            FORMAT_YYYY_MM_DD -> App.applicationContext().getString(R.string.simpledateformat_yyyy_MM_dd)
            FORMAT_HH_MM -> App.applicationContext().getString(R.string.simpledateformat_HH_mm)
            else -> null
        }
    , Locale.US).format(this)

}

//檢查要轉 Float 的字串若為 null 或 empty 則給予 0f 初始值
fun String?.toFloatFormat(): Float {

    return when (this.isNullOrEmpty()){
        true -> 0f
        else -> this.toFloat()
    }
}

fun Float?.toDemicalPoint(point: Int):String {

    return "%.${point}f".format(this)
}

const val FORMAT_MM_DD: Int = 0x01
const val FORMAT_YYYY_MM_DD: Int = 0x02
const val FORMAT_YYYY_MM: Int = 0x03
const val FORMAT_HH_MM: Int = 0x04


