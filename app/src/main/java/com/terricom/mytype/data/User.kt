package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserMT (
    val user_email: String ?= null,
    val user_name: String ?= null,
    val user_picture: String ?= null,
    val Foodie: List<Foodie> ?=null,
    val Shape: List<Shape> ?=null,
    val Sleep: List<Sleep> ?= null,
    val foodlist: List<String> ?= null,
    val nutritionlist: List<String> ?= null
): Parcelable