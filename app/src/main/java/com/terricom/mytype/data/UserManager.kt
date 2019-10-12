package com.terricom.mytype.data

import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.App
import com.terricom.mytype.tools.Logger

private inline fun SharedPreferences.edit(
    operation: (SharedPreferences.Editor
    ) -> Unit) {

    val editor = edit()
    operation(editor)
    editor.apply()
}

const val tagUserToken = "token"
const val tagUserName = "name"
const val tagUserUid = "uid"
const val tagUserPhoto = "pic"
const val tagUserMail = "mail"
const val tagPuzzleNew = "times"
const val tagPuzzleOld = "puzzle"


object UserManager {

    var prefs : SharedPreferences? = App.instance?.getSharedPreferences(tagUserToken, 0)


    var userToken: String? = null
        get() {
            return prefs?.getString(
                tagUserToken, "" )
        }
        set(value) {
            field = prefs?.edit()?.putString(
                tagUserToken, value)?.apply().toString()
        }

    var name : String? = null
        get(){
            return  prefs?.getString(
                tagUserName, "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                tagUserName,value)?.apply().toString()
            Log.i("UserManager.Name", value)
        }

    var picture : String? = null
        get(){
            return prefs?.getString(
                tagUserPhoto, "https://firebasestorage.googleapis.com/v0/b/mytype-201909.appspot.com/o/images%2Fprojects%2Ficon_my_type_round.png?alt=media&token=63ef607b-0977-4312-a28a-e59d0cb0cf3f")
        }
        set(value){
            field = prefs?.edit()?.putString(
                tagUserPhoto,value)?.apply().toString()
            Log.i("UserManager.Picture", value)
        }

    var uid : String? = null
        get(){
            return prefs?.getString(
                tagUserUid, "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                tagUserUid,value)?.apply().toString()
            Log.i("UserManager.Uid", value)
        }

    var mail : String? = null
        get(){
            return prefs?.getString(
                tagUserMail, "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                tagUserMail,value)?.apply().toString()
            Log.i("UserManager.mail", value)
        }

    //處理新用戶獲得拼圖的情況（ 只顯示一次通知 ）
    var getPuzzleNewUser : String? = null
        get(){
            return prefs?.getString(
                tagPuzzleNew, "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                tagPuzzleNew,value)?.apply().toString()
            Log.i("UserManager.times", value!!.toString())
        }

    //處理老用戶獲得拼圖的情況（ 只顯示一次通知 ）
    var getPuzzleOldUser: String? = null
        get(){
            return prefs?.getString(
                tagPuzzleOld, "")
        }
        set(value){
            field = prefs?.edit()?.putString(
                tagPuzzleOld,value)?.apply().toString()
            Log.i("UserManager.puzzle", value!!.toString())
        }

    fun isLogin(): Boolean{

        Logger.i("UserManager uid = $uid")
        uid?.let {
            USER_REFERENCE = FirebaseFirestore.getInstance().collection(FirebaseKey.COLLECTION_USERS)
                .document(it)
        }

        return (!userToken.isNullOrEmpty())
    }

    var USER_REFERENCE: DocumentReference ?= null

}