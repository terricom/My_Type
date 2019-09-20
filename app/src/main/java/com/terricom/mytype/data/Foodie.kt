package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Foodie (
    val timestamp: Date ?= null,
    val photo: String ?= "",
    var foods: List<String> ?= null,
    val water: Float ?= null,
    val oil: Float ?= null,
    val vegetable: Float ?= null,
    val protein: Float ?= null,
    val fruit: Float ?= null,
    val carbon: Float ?= null,
    var nutritions: List<String> ?= null,
    var docId: String ?= "",
    val memo: String ?=""
): Parcelable

@Parcelize
data class FoodieSum (
    val day: String ?= "",
    val water: Float ?= null,
    val oil: Float ?= null,
    val vegetable: Float ?= null,
    val protein: Float ?= null,
    val fruit: Float ?= null,
    val carbon: Float ?= null
): Parcelable