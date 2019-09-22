package com.terricom.mytype.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.terricom.mytype.*
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.activity_main.*

class LoginFragment: Fragment() {

    private val viewModel : LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)}
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth


    override fun onStart() {
        super.onStart()
//        (activity as MainActivity).toolbar.visibility = View.GONE
        (activity as MainActivity).bottom_nav_view.visibility = View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        if (UserManager.userToken!!.isNotEmpty()){
            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
            (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
            (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
            (activity as MainActivity).fab.visibility = View.VISIBLE
        } else {

        binding.buttonLoginFacebook.setOnClickListener {

            viewModel.loginFB()
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile", "user_friends"))
        }



        viewModel.loginFacebook.observe(this, Observer {
            if (it){
//                    LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile", "user_friends"))
                    handleFacebookAccessToken(viewModel.accessToken as AccessToken)
//                LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
//                if (it){
                    Logger.i("UserManager.userToken = ${UserManager.userToken}")


                viewModel.onLoginFacebookCompleted()
            }
        })

        auth = FirebaseAuth.getInstance()
        }

        return binding.root
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Logger.d( "handleFacebookAccessToken:${token.token}")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Logger.d( "signInWithCredential:success")
                    var user = auth.currentUser
                    viewModel.checkUser(user!!.uid)
                    Logger.i("UserManager.userToken onActivityResult=${UserManager.userToken}")
                    if (UserManager.userToken!!.isNotEmpty()){
                        findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                        (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
                        (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                        (activity as MainActivity).fab.visibility = View.VISIBLE
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Logger.w("signInWithCredential:failure ${task.exception}")
                    Toast.makeText(
                        App.applicationContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.callbackManager?.onActivityResult(requestCode, resultCode, data)


        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout3.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout4.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false

    }


}