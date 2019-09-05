package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Shape (
    val timestamp: Date ?= null,
    val weight: Float ?= null,
    val bodyFat: Float ?= null,
    val bodyWater: Float ?= null,
    val muscle: Float ?= null,
    val bodyAge: Float ?= null,
    val tdee: Float ?= null
): Parcelable