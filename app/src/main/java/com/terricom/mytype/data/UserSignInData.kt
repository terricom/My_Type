package com.terricom.mytype.data

import androidx.room.Entity
import com.squareup.moshi.Json

data class UserSignInData (
    @Json(name = "data") val data: UserData
)

data class UserData(
    var access_token: String?,
    var access_expired: Int,
    var user: User
)

@Entity
data class User(
    var id: Int,
    var provider: String?,
    var name: String?,
    var email: String?,
    var picture:String
)