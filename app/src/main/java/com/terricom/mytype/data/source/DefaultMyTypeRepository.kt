package com.terricom.mytype.data.source

import androidx.lifecycle.LiveData
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import java.sql.Timestamp

class DefaultMyTypeRepository(private val myTypeRemoteDataSource: MyTypeDataSource,
                              private val myTypeLocalDataSource: MyTypeDataSource
): MyTypeRepository {

    override suspend fun isGoalInLocal(id: String): Boolean {
        return myTypeLocalDataSource.isGoalInLocal(id = id)
    }

    override suspend fun insertGoal(goal: Goal) {
        return myTypeLocalDataSource.insertGoal(goal = goal)
    }

    override suspend fun updateGoal(goal: Goal) {
        return myTypeLocalDataSource.updateGoal(goal = goal)
    }

    override suspend fun clearGoal() {
        return myTypeLocalDataSource.clearGoal()
    }

    override fun getGoal(): LiveData<List<Goal>> {
        return myTypeLocalDataSource.getGoal()
    }

    override suspend fun queryFoodie(key: String, type: String): List<Foodie> {
        return myTypeRemoteDataSource.queryFoodie(key= key, type= type)
    }

    override suspend fun updatePuzzle(): Int {
        return myTypeRemoteDataSource.updatePuzzle()
    }

    override suspend fun deleteObjects(collection: String, any: Any) {
        return myTypeRemoteDataSource.deleteObjects(collection= collection, any = any )
    }

    override suspend fun getObjects(collection: String, start: Timestamp, end: Timestamp): List<Any> {
        return myTypeRemoteDataSource.getObjects(collection= collection, start = start, end = end)
    }

}

