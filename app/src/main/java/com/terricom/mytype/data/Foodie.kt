package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
data class Foodie (
    val timestamp: Timestamp,
    val photo: String,
    val foods: List<String>,
    val water: Int,
    val oil: Int,
    val vegetable: Int,
    val protein: Int,
    val fruit: Int,
    val carbon: Int
): Parcelable