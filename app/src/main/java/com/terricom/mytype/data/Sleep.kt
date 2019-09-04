package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Sleep (
    val wakeUp: Date ?= null ,
    val sleepHr: Long ?= null,
    val goToBed: Date ?= null
): Parcelable
