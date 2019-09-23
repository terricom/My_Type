package com.terricom.mytype.data

import android.content.SharedPreferences
import android.util.Log
import com.terricom.mytype.App

private inline fun SharedPreferences.edit(operation:
                                              (SharedPreferences.Editor) -> Unit) {
    val editor = edit()
    operation(editor)
    editor.apply()
}


object UserManager {

    var prefs : SharedPreferences? = App.instance?.getSharedPreferences("token", 0)


    var userToken: String? = null
        get() {
            return prefs?.getString("token", "" )
        }
        set(value) {
            field = prefs?.edit()?.putString("token", value)?.apply().toString()
            Log.i("Call api", value)
        }

    var name : String? = null
        get(){
            return  prefs?.getString("name", "")
        }
        set(value){
            field = prefs?.edit()?.putString("name",value)?.apply().toString()
            Log.i("UserManager.Name", value)
        }

    var picture : String? = null
        get(){
            return prefs?.getString("pic", "")
        }
        set(value){
            field = prefs?.edit()?.putString("pic",value)?.apply().toString()
            Log.i("UserManager.Picture", value)
        }

    var uid : String? = null
        get(){
            return prefs?.getString("uid", "")
        }
        set(value){
            field = prefs?.edit()?.putString("uid",value)?.apply().toString()
            Log.i("UserManager.Uid", value)
        }

    var mail : String? = null
        get(){
            return prefs?.getString("mail", "")
        }
        set(value){
            field = prefs?.edit()?.putString("mail",value)?.apply().toString()
            Log.i("UserManager.mail", value)
        }


}