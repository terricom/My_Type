package com.terricom.mytype.data

import android.content.SharedPreferences
import android.util.Log
import com.terricom.mytype.App
import com.terricom.mytype.R

private inline fun SharedPreferences.edit(
    operation: (SharedPreferences.Editor
    ) -> Unit) {

    val editor = edit()
    operation(editor)
    editor.apply()
}

object UserManager {

    var prefs : SharedPreferences? = App.instance?.getSharedPreferences(
        App.applicationContext().getString(R.string.user_manager_token), 0)


    var userToken: String? = null
        get() {
            return prefs?.getString(
                App.applicationContext().getString(R.string.user_manager_token), "" )
        }
        set(value) {
            field = prefs?.edit()?.putString(
                App.applicationContext().getString(R.string.user_manager_token), value)?.apply().toString()
            Log.i("UserManager.token", value)
        }

    var name : String? = null
        get(){
            return  prefs?.getString(
                App.applicationContext().getString(R.string.user_manager_name), "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                App.applicationContext().getString(R.string.user_manager_name),value)?.apply().toString()
            Log.i("UserManager.Name", value)
        }

    var picture : String? = null
        get(){
            return prefs?.getString(
                App.applicationContext().getString(R.string.user_manager_pic), "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                App.applicationContext().getString(R.string.user_manager_pic),value)?.apply().toString()
            Log.i("UserManager.Picture", value)
        }

    var uid : String? = null
        get(){
            return prefs?.getString(
                App.applicationContext().getString(R.string.user_manager_uid), "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                App.applicationContext().getString(R.string.user_manager_uid),value)?.apply().toString()
            Log.i("UserManager.Uid", value)
        }

    var mail : String? = null
        get(){
            return prefs?.getString(
                App.applicationContext().getString(R.string.user_manager_mail), "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                App.applicationContext().getString(R.string.user_manager_mail),value)?.apply().toString()
            Log.i("UserManager.mail", value)
        }

    //處理新用戶獲得拼圖的情況（ 只顯示一次通知 ）
    var getPuzzleNewUser : String? = null
        get(){
            return prefs?.getString(
                App.applicationContext().getString(R.string.user_manager_times), "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                App.applicationContext().getString(R.string.user_manager_times),value)?.apply().toString()
            Log.i("UserManager.times", value!!.toString())
        }

    //處理老用戶獲得拼圖的情況（ 只顯示一次通知 ）
    var getPuzzleOldUser: String? = null
        get(){
            return prefs?.getString(
                App.applicationContext().getString(R.string.user_manager_puzzle), "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                App.applicationContext().getString(R.string.user_manager_puzzle),value)?.apply().toString()
            Log.i("UserManager.puzzle", value!!.toString())
        }

    fun isLogin(): Boolean{
        return (!userToken.isNullOrEmpty())
    }


}