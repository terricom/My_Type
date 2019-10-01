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
import com.terricom.mytype.data.User
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.tools.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.json.JSONException
import java.io.IOException






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

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

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

    private var mProfileTracker: ProfileTracker ?= null

    private val _navigateToLoginSuccess = MutableLiveData<User>()

    val navigateToLoginSuccess: LiveData<User>
        get() = _navigateToLoginSuccess


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
                                val id = `object`.getLong("id")
                                val name = `object`.getString("name")
                                val email = `object`.getString("email")
                                Logger.d( "Facebook id:$id")
                                Logger.d( "Facebook name:$name")
                                Logger.d( "Facebook email:$email")

                                    // 此時如果登入成功，就可以順便取得用戶大頭照
                                val profile = Profile.getCurrentProfile()

                                var userPhoto = profile.getProfilePictureUri(300, 300)

                                    UserManager.userToken = id.toString()
                                    UserManager.name = name
                                    UserManager.picture = userPhoto.toString()
                                    user_name = name
                                    user_email = email
                                    user_picture = userPhoto.toString()

                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email")
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

    val db = FirebaseFirestore.getInstance()
    val users = db.collection("Users")

    fun checkUser(uid: String){

        //發文功能
        val userData = hashMapOf(
            "user_name" to UserManager.name,
            "user_picture" to UserManager.picture,
            "user_email" to UserManager.mail,
            "foodlist" to listOf<String>(),
            "nutritionlist" to listOf<String>()
        )

        var newOne = ""

        users.get()
            .addOnSuccessListener { result->
                val items = mutableListOf<User>()
                for (doc in result){
                    //老用戶登入
                    if (doc.id == uid ){
                        var pref: SharedPreferences? = null
                        pref = App.instance?.getSharedPreferences("uid", 0)
                        pref!!.edit().putString(uid, "")
                        UserManager.uid = uid
                        items.add(doc.toObject(User::class.java))
                        _user.value = doc.toObject(User::class.java)

                    //其他老用戶
                    }else{
                        var pref: SharedPreferences? = null
                        pref = App.instance?.getSharedPreferences("uid", 0)
                        pref!!.edit().putString(uid, "")
                        UserManager.uid = uid
                        newOne = uid

                    }

                }
                //全新用戶
                Logger.i("items User = $items")
                if (items.size == 1){
                }else if (items.isEmpty()){
                    users.document(newOne).set(userData)
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


}