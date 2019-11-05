package com.terricom.mytype.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.terricom.mytype.data.Goal

@Dao
interface MyTypeDatabaseDao {

    @Insert
    fun insert(goal: Goal)

    @Update
    fun update(goal: Goal)

    @Query("DELETE from goal_latest_table WHERE goal_doc_id = :id")
    fun delete(id: String)

    @Query("DELETE FROM goal_latest_table")
    fun clear()

    @Query("SELECT * FROM goal_latest_table")
    fun getAllProducts():
            LiveData<List<Goal>>

    @Query("SELECT * from goal_latest_table WHERE goal_doc_id = :id")
    fun get(id: String): Goal?

}