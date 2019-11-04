package com.terricom.mytype.data

import java.sql.Timestamp

interface FirebaseDataSource{

    suspend fun queryFoodie(key: String, type: String): List<Foodie>

    suspend fun getObjects(collection: String, start: Timestamp, end: Timestamp): List<Any>

    suspend fun deleteObjects(collection: String, any: Any)

    suspend fun updatePuzzle(): Int
}