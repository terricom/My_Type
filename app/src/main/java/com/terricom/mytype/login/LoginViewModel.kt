package com.terricom.mytype.login

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.terricom.mytype.App.Companion.instance
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

    var callbackManager: CallbackManager? = null
    var loginManager: LoginManager? = null

    fun loginFB() {
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    val FB_Token = "token from facebook"
                    var pref: SharedPreferences? = null
                    pref = instance?.getSharedPreferences("token", 0)
                    val userAccessToken = pref?.getString(FB_Token, "")
                    pref!!.edit().putString(FB_Token, "")


                    Log.d("FB", "FB token: " + loginResult.accessToken.token)


                    var viewModelJob = Job()
                    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


                    fun getProperties(token: String) {
                        coroutineScope.launch {

                            var getPropertiesDeferred =
                                RetrofitApi.retrofitService.postUserSignin(access_token = token)
                            try {
                                // this will run on a thread managed by Retrofit
                                val listResult = getPropertiesDeferred.await()


                                Log.i("FB", "Tried to get user value")
                                UserManager.userToken = listResult.data.toString()
                                UserManager.name = listResult.data.user.name
                                UserManager.picture = listResult.data.user.picture

                                Log.d("ProfileViewModel","print list result = ${UserManager.name}")


                            } catch (e: Exception) {
                                Log.i("FB", "exception=${e.message}")
                            }

                        }
                    }


                    getProperties(loginResult.accessToken.token)

//                    showCenterDialog()
//                    handler.postDelayed({
//                        (activity as MainActivity).binding.navView.selectedItemId = R.id.navigation_member
//
//                        centerDialog.dismiss()
//
//
//                    }, 3000)


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
}