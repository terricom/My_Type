package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Shape (
    val timestamp: Date ?= null,
    val weight: Long ?= null,
    val bodyFat: Long ?= null,
    val bodyWater: Long ?= null,
    val muscle: Long ?= null,
    val bodyAge: Long ?= null,
    val tdee: Long ?= null
): Parcelable