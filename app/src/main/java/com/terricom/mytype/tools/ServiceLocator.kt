package com.terricom.mytype.tools

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.terricom.mytype.data.DefaultFirebaseRepository
import com.terricom.mytype.data.FirebaseRemoteDataSource
import com.terricom.mytype.data.FirebaseRepository

object ServiceLocator {

    @Volatile
    var firebaseRepository: FirebaseRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): FirebaseRepository {
        synchronized(this) {
            return firebaseRepository
                ?: firebaseRepository
                ?: createStylishRepository(context)
        }
    }

    private fun createStylishRepository(context: Context): FirebaseRepository {
        return DefaultFirebaseRepository(FirebaseRemoteDataSource
//            ,
//            createLocalDataSource(context)
        )
    }

//    private fun createLocalDataSource(context: Context): FirebaseDataSource {
//        return StylishLocalDataSource(context)
//    }
}