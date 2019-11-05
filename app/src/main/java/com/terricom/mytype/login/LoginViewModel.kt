package com.terricom.mytype.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.App
import com.terricom.mytype.data.*
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.Logger
import org.json.JSONException
import java.io.IOException

class LoginViewModel(private val myTypeRepository: MyTypeRepository): ViewModel() {

    // Handle leave login
    private val _loginFacebook = MutableLiveData<Boolean>()
    val loginFacebook: LiveData<Boolean>
        get() = _loginFacebook

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    var callbackManager: CallbackManager ?= null
    var loginManager: LoginManager ?= null
    var accessToken: AccessToken ?= null

    fun loginFB() {
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {

                    accessToken = loginResult.accessToken
                    val graphRequest = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { `object`, response ->
                        try {
                            if (response.connection.responseCode == 200) {

                                UserManager.userToken = `object`.getLong(id).toString()
                                UserManager.name = `object`.getString(name)
                                UserManager.mail = `object`.getString(email)
                                Profile.getCurrentProfile()?.let {
                                    UserManager.picture = it.getProfilePictureUri(300, 300).toString()
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                        val parameters = Bundle()
                        parameters.putString(fields, "$id,$name,$email")
                        graphRequest.parameters = parameters
                        graphRequest.executeAsync()
                        loginFacebook()

                }

                override fun onCancel() {
                    Log.d("FB", "FB cancel")
                }
                override fun onError(error: FacebookException?) {
                    Logger.i("loginFB error = $error")
                }

            })
    }


    private fun loginFacebook() {
        _loginFacebook.value = true
    }
    fun onLoginFacebookCompleted() {
        _loginFacebook.value = false
    }

    fun checkUser(uid: String){

        //發文功能
        val userData = hashMapOf(
            FirebaseKey.COLUMN_USER_NAME to UserManager.name,
            FirebaseKey.COLUMN_USER_PICTURE to UserManager.picture,
            FirebaseKey.COLUMN_USER_EMAIL to UserManager.mail,
            FirebaseKey.COLUMN_USER_FOOD_LIST to listOf<String>(),
            FirebaseKey.COLUMN_USER_NUTRITION_LIST to listOf<String>()
        )

        FirebaseFirestore.getInstance()
            .collection(FirebaseKey.COLLECTION_USERS)
            .get()
            .addOnSuccessListener { result->
                val items = mutableListOf<User>()
                for (doc in result){
                    //老用戶登入
                    if (doc.id == uid ){
                        var pref: SharedPreferences? = null
                        pref = App.instance?.getSharedPreferences(tagUserUid, 0)
                        pref?.let {
                            it.edit().putString(uid, "")
                        }
                        UserManager.uid = uid
                        items.add(doc.toObject(User::class.java))
                        _user.value = doc.toObject(User::class.java)

                    }
                }
                //全新用戶
                if (items.isEmpty()){
                    FirebaseFirestore.getInstance().collection(FirebaseKey.COLLECTION_USERS).document(uid).set(userData)
                    _user.value = User(
                        UserManager.mail,
                        UserManager.name,
                        UserManager.picture,
                        listOf(),
                        listOf(),
                        listOf(),
                        listOf(),
                        listOf(),
                        listOf(),
                        listOf()
                    )
                }
            }
    }

    companion object {
        val id = "id"
        val name = "name"
        val email = "email"
        val fields = "fields"
    }

}