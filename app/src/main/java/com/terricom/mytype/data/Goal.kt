package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Goal (
    val timestamp: Date?= null,
    val deadline: Date?= null,
    val water: Float ?= null,
    val oil: Float ?= null,
    val vegetable: Float ?= null,
    val protein: Float ?= null,
    val fruit: Float ?= null,
    val carbon: Float ?= null,
    val weight: Float ?= null,
    val bodyFat: Float ?= null,
    val muscle: Float ?= null,
    val cheerUp: String ?="",
    var docId: String ?= ""
): Parcelable