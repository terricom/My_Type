package com.terricom.mytype.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.data.source.MyTypeDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Timestamp

class MyTypeLocalDataSource(val context: Context) : MyTypeDataSource{
    override suspend fun setOrUpdateObjects(collection: String, any: Any, documentId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getObjects(
        collection: String,
        start: Timestamp,
        end: Timestamp
    ): List<Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteObjects(collection: String, any: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun updatePuzzle(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun queryFoodie(key: String, type: String): List<Foodie> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGoal(): LiveData<List<Goal>> {
        return MyTypeDatabase.getInstance(context).myTypeDatabaseDao.getAllProducts()
    }

    override suspend fun insertGoal(goal: Goal) {
        withContext(Dispatchers.IO) {
            MyTypeDatabase.getInstance(context).myTypeDatabaseDao.insert(goal)
        }
    }

    override suspend fun updateGoal(goal: Goal) {
        withContext(Dispatchers.IO) {
            MyTypeDatabase.getInstance(context).myTypeDatabaseDao.update(goal)
        }
    }

    override suspend fun clearGoal() {
        withContext(Dispatchers.IO) {
            MyTypeDatabase.getInstance(context).myTypeDatabaseDao.clear()
        }
    }

    override suspend fun isGoalInLocal(id: String): Boolean {
        return withContext(Dispatchers.IO) {
            MyTypeDatabase.getInstance(context).myTypeDatabaseDao.get(id) != null
        }
    }
}