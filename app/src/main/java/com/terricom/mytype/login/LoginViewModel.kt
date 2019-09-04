package com.terricom.mytype.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.firestore.FirebaseFirestore
import com.terricom.mytype.App
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.internet.RetrofitApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    // Handle leave login
    private val _loginFacebook = MutableLiveData<Boolean>()

    val loginFacebook: LiveData<Boolean>
        get() = _loginFacebook

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    var callbackManager: CallbackManager ?= null
    var loginManager: LoginManager ?= null
    var accessToken: AccessToken ?= null

    var user_name = ""
    var user_email = ""
    var user_picture = ""


    fun loginFB() {
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    val FB_Token = "token from facebook"
                    var pref: SharedPreferences? = null
                    pref = App.instance?.getSharedPreferences("token", 0)
                    val userAccessToken = pref?.getString(FB_Token, "")
                    pref!!.edit().putString(FB_Token, "")

                    accessToken = loginResult.accessToken
                    Log.d("FB", "FB token: " + loginResult.accessToken.token)
//                    handleFacebookAccessToken(loginResult.accessToken)


                    var viewModelJob = Job()
                    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

                    fun getProperties(token: String) {
                        coroutineScope.launch {

                            var getPropertiesDeferred =
                                RetrofitApi.retrofitService.postUserSignin(access_token = token)
                            try {
                                // this will run on a thread managed by Retrofit
                                val listResult = getPropertiesDeferred.await()


                                UserManager.userToken = listResult.data.toString()
                                UserManager.name = listResult.data.user.name
                                UserManager.picture = listResult.data.user.picture
                                user_name = listResult.data.user.name as String
                                user_email = listResult.data.user.email as String
                                user_picture = listResult.data.user.picture as String
                                loginFacebook()

                                Log.d("ProfileViewModel","print list result = ${UserManager.name}")


                            } catch (e: Exception) {
                                Log.i("FB", "exception=${e.message}")
                            }

                        }
                    }


                    getProperties(loginResult.accessToken.token)
                }

                override fun onCancel() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    Log.d("FB", "FB cancel")
                }

                override fun onError(error: FacebookException?) {
                }

            })
    }


    private fun loginFacebook() {
        _loginFacebook.value = true
    }
    fun onLoginFacebookCompleted() {
        _loginFacebook.value = null
    }

    val db = FirebaseFirestore.getInstance()
    val user = db.collection("Users")

    fun checkUser(uid: String){

        //發文功能
        val userData = hashMapOf(
            "user_name" to user_name,
            "user_picture" to user_picture,
            "user_email" to user_email
        )

        user.get()
            .addOnSuccessListener { result->
                for (doc in result){
                    if (doc.id == uid ){
                        var pref: SharedPreferences? = null
                        pref = App.instance?.getSharedPreferences("uid", 0)
                        pref!!.edit().putString(uid, "")
                        UserManager.uid = uid
                    }else{
                        var pref: SharedPreferences? = null
                        pref = App.instance?.getSharedPreferences("uid", 0)
                        pref!!.edit().putString(uid, "")
                        user.document(uid).set(userData)
                        UserManager.uid = uid

                    }
                }

            }


    }

}