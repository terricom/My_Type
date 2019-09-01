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

        // setup the broadcast action namespace string which will
        // be used to notify upload status.
        // Gradle automatically generates proper variable as below.
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID
        // Or, you can define it manually.
        UploadService.NAMESPACE = "com.greenhackers.imageuploadtest"

    }
}