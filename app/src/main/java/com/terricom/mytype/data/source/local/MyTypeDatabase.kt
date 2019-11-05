package com.terricom.mytype.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.terricom.mytype.data.Goal

@Database(entities = [Goal::class], version = 1, exportSchema = false)
abstract class MyTypeDatabase: RoomDatabase(){

    abstract val myTypeDatabaseDao: MyTypeDatabaseDao

    companion object{

        @Volatile
        private var INSTANCE: MyTypeDatabase? = null

        fun getInstance(context: Context): MyTypeDatabase {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyTypeDatabase::class.java,
                        "my_type_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }
                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }
}