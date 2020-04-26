package com.terricom.mytype.data.source

import androidx.lifecycle.LiveData
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.Result
import java.sql.Timestamp

class DefaultMyTypeRepository(private val myTypeRemoteDataSource: MyTypeDataSource,
                              private val myTypeLocalDataSource: MyTypeDataSource
): MyTypeRepository {

    override suspend fun setOrUpdateObjects(collection: String, any: Any, documentId: String) {
        return myTypeRemoteDataSource.setOrUpdateObjects(collection = collection, any = any, documentId = documentId)
    }

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

    override suspend fun queryFoodie(key: String, type: String): Result<List<Foodie>> {
        return myTypeRemoteDataSource.queryFoodie(key= key, type= type)
    }

    override suspend fun updatePuzzle(): Int {
        return myTypeRemoteDataSource.updatePuzzle()
    }

    override suspend fun deleteObjects(collection: String, any: Any) {
        return myTypeRemoteDataSource.deleteObjects(collection= collection, any = any )
    }

    override suspend fun <T: Any> getObjects(collection: String, start: Timestamp, end: Timestamp): Result<List<T>> {
        return myTypeRemoteDataSource.getObjects(collection= collection, start = start, end = end)
    }

}

