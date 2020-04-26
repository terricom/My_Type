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
import com.terricom.mytype.App
import com.terricom.mytype.R
import com.terricom.mytype.data.*
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.tools.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.IOException
import java.sql.Timestamp

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

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun checkUser(uid: String){

        val userData = hashMapOf(
            FirebaseKey.COLUMN_USER_NAME to UserManager.name,
            FirebaseKey.COLUMN_USER_PICTURE to UserManager.picture,
            FirebaseKey.COLUMN_USER_EMAIL to UserManager.mail,
            FirebaseKey.COLUMN_USER_FOOD_LIST to listOf<String>(App.applicationContext().getString(R.string.foodie_foodlist_bento), App.applicationContext().getString(R.string.foodie_foodlist_pasta), App.applicationContext().getString(R.string.foodie_foodlist_sandwich)),
            FirebaseKey.COLUMN_USER_NUTRITION_LIST to listOf<String>(App.applicationContext().getString(R.string.foodie_nutritionlist_vitaminb), App.applicationContext().getString(R.string.foodie_nutritionlist_lutein))
        )

        coroutineScope.launch {

            val userResult = myTypeRepository.getObjects<User>(FirebaseKey.COLLECTION_USERS, Timestamp(946656000), Timestamp(4701859200))

            if (userResult is Result.Success) {
                for (user in userResult.data) {
                    if (user.user_uid == uid) {
                        var pref: SharedPreferences? = null
                        pref = App.instance?.getSharedPreferences(tagUserUid, 0)
                        pref?.let {
                            it.edit().putString(uid, "")
                        }
                        UserManager.uid = uid
                        _user.value = user
                    }
                }

                if (userResult.data.none { it.user_uid == uid }){
                    myTypeRepository.setOrUpdateObjects(FirebaseKey.COLLECTION_USERS, userData, uid)
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
                        listOf(),
                        uid
                    )
                }

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