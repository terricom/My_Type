package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
data class Sleep (
    val wakeUp: Timestamp,
    val sleepHour: Int,
    val goToBed: Timestamp
): Parcelable
