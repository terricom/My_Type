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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.terricom.mytype.*
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.databinding.FragmentLoginBinding
import com.terricom.mytype.tools.Logger
import kotlinx.android.synthetic.main.activity_main.*

class LoginFragment: Fragment() {

    private val viewModel : LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)}
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private var currentUser : FirebaseUser ?= null


    override fun onStart() {
        super.onStart()
//        (activity as MainActivity).toolbar.visibility = View.GONE
        (activity as MainActivity).bottom_nav_view.visibility = View.GONE
        // Configure Google Sign In
        currentUser = auth.currentUser
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        auth = FirebaseAuth.getInstance()

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
        binding.buttonLoginGoogle.setOnClickListener {
//            initGoogleClient(googleSignInClient)
            signIn()
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


    val RC_SIGN_IN: Int = 1


    private lateinit var googleSignInClient: GoogleSignInClient

    private fun signIn() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(App.applicationContext(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.callbackManager?.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            auth = FirebaseAuth.getInstance()
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                UserManager.userToken = account!!.idToken
                UserManager.name = account.displayName
                UserManager.picture = account.photoUrl.toString()
                UserManager.mail = account.email

                firebaseAuthWithGoogle(account)
                Logger.i("ServerAuthCode =${account.serverAuthCode} account.id =${account.id}")
                if (UserManager.userToken!!.isNotEmpty()){
                    findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                    (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
                    (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                    (activity as MainActivity).fab.visibility = View.VISIBLE
                }

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Logger.w("Google sign in failed : Exception = $e")
                // ...
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Logger.d("firebaseAuthWithGoogle: ${acct.id!!}")

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener((activity as MainActivity)) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Logger.d("signInWithCredential:success")
                    val user = auth.currentUser
                    viewModel.checkUser(user!!.uid)
                } else {
                    // If sign in fails, display a message to the user.
                    Logger.w("signInWithCredential:failure ${task.exception}")
                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }

            }
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