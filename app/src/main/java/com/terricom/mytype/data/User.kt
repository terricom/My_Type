package com.terricom.mytype.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val user_email: String ?= null,
    val user_name: String ?= null,
    val user_picture: String ?= null,
    val Foodie: List<Foodie> ?=null,
    val Shape: List<Shape> ?=null,
    val Sleep: List<Sleep> ?= null,
    val Goal: List<Goal> ?= null,
    val Puzzle : List<Puzzle> ?= null,
    val food_list: List<String> ?= null,
    val nutrition_list: List<String> ?= null
): Parcelable