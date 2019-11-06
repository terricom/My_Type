package com.terricom.mytype.tools

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.terricom.mytype.data.source.DefaultMyTypeRepository
import com.terricom.mytype.data.source.MyTypeDataSource
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.data.source.local.MyTypeLocalDataSource
import com.terricom.mytype.data.source.remote.MyTypeRemoteDataSource

object ServiceLocator {

    @Volatile
    var myTypeRepository: MyTypeRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): MyTypeRepository {
        synchronized(this) {
            return myTypeRepository
                ?: myTypeRepository
                ?: createMyTypeRepository(context)
        }
    }

    private fun createMyTypeRepository(context: Context): MyTypeRepository {
        return DefaultMyTypeRepository(MyTypeRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): MyTypeDataSource {
        return MyTypeLocalDataSource(context)
    }
}