package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Foodie (
    val timestamp: Date ?= null,
    val photo: String ?= "",
    val foods: List<String> ?= null,
    val water: Float ?= null,
    val oil: Float ?= null,
    val vegetable: Float ?= null,
    val protein: Float ?= null,
    val fruit: Float ?= null,
    val carbon: Float ?= null,
    val nutritions: List<String> ?= null
): Parcelable