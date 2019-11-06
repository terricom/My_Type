package com.terricom.mytype

import android.app.Application
import android.content.Context
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.ServiceLocator

// To solve providing a non-null ‘prefs’ object to our entire app
class App : Application() {

    val myTypeRepository: MyTypeRepository
        get() = ServiceLocator.provideTasksRepository(this)

    companion object {

        var instance : App? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }
}