package com.terricom.mytype

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.terricom.mytype.tools.Logger
import java.text.SimpleDateFormat
import java.util.*

fun Date?.toDateFormat(dateFormat: Int): String {

    return SimpleDateFormat(
        when (dateFormat){
            FORMAT_MM_DD -> App.applicationContext().getString(R.string.simpledateformat_MM_dd)
            FORMAT_YYYY_MM -> App.applicationContext().getString(R.string.simpledateformat_yyyy_MM)
            FORMAT_YYYY_MM_DD -> App.applicationContext().getString(R.string.simpledateformat_yyyy_MM_dd)
            FORMAT_HH_MM -> App.applicationContext().getString(R.string.simpledateformat_HH_mm)
            FORMAT_HH_MM_SS_FFFFFFFFF -> App.applicationContext().getString(R.string.simpledateformat_HH_mm_ss_fffffffff, "000000000")
            FORMAT_YYYY_MM_DDHHMMSS -> App.applicationContext().getString(R.string.simpledateformat_yyyy_MM_dd_HHmmss)
            FORMAT_YYYY_MM_DD_HH_MM_SS_FFFFFFFFF -> App.applicationContext().getString(R.string.simpledateformat_yyyy_MM_dd_HH_mm_ss_fffffffff, "000000000")
            else -> null
        }
    , Locale.US).format(this)

}

//檢查要轉 Float 的字串若為 null 或 empty 則給予 0f 初始值
fun String?.toFloatFormat(): Float {

    Logger.i("String?.toFloatFormat = $this")
    return when (this.isNullOrEmpty()){
        true -> 0f
        else -> {
            if (this == "n"){
                0f
            }else {
                this.toFloat()
            }
        }
    }
}

fun Float?.toDemicalPoint(point: Int):String {

    return "%.${point}f".format(this ?: 0f)
}

fun isConnected(): Boolean{
    val connectivityManager = App.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (connectivityManager is ConnectivityManager) {
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        networkInfo?.isConnected ?: false
    } else false
}


const val FORMAT_MM_DD: Int = 0x01
const val FORMAT_YYYY_MM_DD: Int = 0x02
const val FORMAT_YYYY_MM: Int = 0x03
const val FORMAT_HH_MM: Int = 0x04
const val FORMAT_HH_MM_SS_FFFFFFFFF: Int = 0x05
const val FORMAT_YYYY_MM_DDHHMMSS: Int = 0x06
const val FORMAT_YYYY_MM_DD_HH_MM_SS_FFFFFFFFF: Int = 0x07


