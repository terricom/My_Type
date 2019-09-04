package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Foodie (
    val timestamp: Date ?= null,
    val photo: String ?= "",
    val foods: List<String> ?= null,
    val water: Long ?= null,
    val oil: Long ?= null,
    val vegetable: Long ?= null,
    val protein: Long ?= null,
    val fruit: Long ?= null,
    val carbon: Long ?= null,
    val nutritions: List<String> ?= null
): Parcelable