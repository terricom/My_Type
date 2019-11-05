package com.terricom.mytype.data.source

import androidx.lifecycle.LiveData
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import java.sql.Timestamp

interface MyTypeRepository {

    suspend fun getObjects(collection: String, start: Timestamp, end: Timestamp): List<Any>

    suspend fun deleteObjects(collection: String, any: Any)

    suspend fun updatePuzzle(): Int

    suspend fun queryFoodie(key: String, type: String): List<Foodie>

    fun getGoal(): LiveData<List<Goal>>

    suspend fun insertGoal(goal: Goal)

    suspend fun updateGoal(goal: Goal)

    suspend fun clearGoal()

    suspend fun isGoalInLocal(id: String): Boolean
}