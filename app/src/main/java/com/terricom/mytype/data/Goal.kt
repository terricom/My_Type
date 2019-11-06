package com.terricom.mytype.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.terricom.mytype.data.source.local.MyTypeConverter
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "goal_latest_table", primaryKeys = ["goal_doc_id"])
@Parcelize
@TypeConverters(MyTypeConverter::class)
data class Goal (

    @ColumnInfo(name = "timestamp")
    val timestamp: Date? = null,
    @ColumnInfo(name = "deadline")
    val deadline: Date? = null,
    @ColumnInfo(name = "goal_water")
    val water: Float? = null,
    @ColumnInfo(name = "goal_oil")
    val oil: Float? = null,
    @ColumnInfo(name = "goal_vegetable")
    val vegetable: Float? = null,
    @ColumnInfo(name = "goal_protein")
    val protein: Float? = null,
    @ColumnInfo(name = "goal_fruit")
    val fruit: Float? = null,
    @ColumnInfo(name = "goal_carbon")
    val carbon: Float? = null,
    @ColumnInfo(name = "goal_weight")
    val weight: Float? = null,
    @ColumnInfo(name = "goal_body_fat")
    val bodyFat: Float? = null,
    @ColumnInfo(name = "goal_muscle")
    val muscle: Float? = null,
    @ColumnInfo(name = "goal_cheer_up")
    val cheerUp: String? ="",
    @ColumnInfo(name = "goal_doc_id")
    var docId: String = ""
): Parcelable