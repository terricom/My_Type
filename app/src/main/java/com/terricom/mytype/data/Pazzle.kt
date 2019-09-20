package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pazzle (
    val position: List<Int>?= null,
    val imgURL: String?= "",
    var docId: String ?= "",
    val timestamp: java.util.Date?= null
): Parcelable