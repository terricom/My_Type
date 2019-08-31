package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
data class Shape (
    val timestamp: Timestamp,
    val weight: Int,
    val bodyfat: Int,
    val bodywater: Int,
    val muscle: Int,
    val bodyage: Int,
    val tdee: Int
): Parcelable