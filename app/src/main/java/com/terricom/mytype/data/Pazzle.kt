package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Pazzle (
    val position: List<Int>?= null,
    val imgURL: String?= "",
    var docId: String ?= "",
    val recordedDates: List<String>?= null,
    val timestamp: Date?= null
): Parcelable