package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
data class Diary (
    val timestamp: Timestamp,
    val sleepHour: Int,
    val goToBed: String,
    val nutritions: List<String>,
    val memo: String
): Parcelable
