package com.terricom.mytype

import android.app.Application
import android.content.Context
import net.gotev.uploadservice.UploadService

// To solve providing a non-null ‘prefs’ object to our entire app
class App : Application() {

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