package com.terricom.mytype.data.source.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.*

class MyTypeConverter {

    @TypeConverter
    fun convertListToJson(list: List<String>?): String? {
        list?.let {
            return Moshi.Builder().build().adapter<List<String>>(List::class.java).toJson(list)
        }
        return null
    }

    @TypeConverter
    fun convertJsonToList(json: String?): List<String>? {
        json?.let {
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            val adapter: JsonAdapter<List<String>> = Moshi.Builder().build().adapter(type)
            return adapter.fromJson(it)
        }
        return null
    }

    @TypeConverter
    fun convertDateToJson(date: Date): String? {
        date?.let {
            return Gson().toJson(date)
        }
        return null
    }

    @TypeConverter
    fun convertJsonToDate(json: String?): Date? {
        json?.let {
            val type = object : TypeToken<Date>() {
            }.type
            return Gson().fromJson(json, type)
        }
        return null
    }

}