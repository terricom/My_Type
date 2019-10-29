package com.terricom.mytype.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.terricom.mytype.*
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.databinding.FragmentLoginBinding
import com.terricom.mytype.tools.Logger
import com.terricom.mytype.tools.getVmFactory
import com.terricom.mytype.tools.isConnected
import kotlinx.android.synthetic.main.activity_main.*

class LoginFragment: Fragment() {

    private val viewModel by viewModels<LoginViewModel> { getVmFactory() }

    private lateinit var binding: FragmentLoginBinding

    override fun onStart() {
        super.onStart()
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
            (activity as MainActivity).back2DiaryFragment()

        } else {

        binding.buttonLoginFacebook.setOnClickListener {
            if (isConnected()){

                LoginManager.getInstance().logInWithReadPermissions(this, listOf(
                    logInWithPermissionsEmail, loginWithPermissionProfile))
                viewModel.loginFB()

            }else {

                Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonLoginGoogle.setOnClickListener {
            if (isConnected()){

                loginGoogle()
            }else {

                Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
            }

        }


        viewModel.loginFacebook.observe(this, Observer {
            if (it){
                handleFacebookAccessToken(viewModel.accessToken as AccessToken)
                viewModel.onLoginFacebookCompleted()
            }
        })

            viewModel.user.observe(this, Observer {
                if (it != null){
                    findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.LOGIN_SUCCESS))
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
                    var user = auth.currentUser
                    user?.let {
                        UserManager.uid = it.uid
                        viewModel.checkUser(it.uid)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        App.applicationContext(), App.applicationContext().getString(R.string.login_fail_toast),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }




    private fun loginGoogle() {

        val googleSignInClient: GoogleSignInClient

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(App.applicationContext(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Companion.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.callbackManager?.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Companion.RC_SIGN_IN) {
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

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Logger.w("Google sign in failed : Exception = $e")
                // ...
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener((activity as MainActivity)) { task ->

                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Logger.d("signInWithCredential:success")

                    val user = auth.currentUser
                    user?.let {
                        UserManager.uid = it.uid
                        viewModel.checkUser(it.uid)
                    }
                } else {
                    Toast.makeText(
                        App.applicationContext(), App.applicationContext().getString(R.string.login_fail_toast),
                        Toast.LENGTH_SHORT).show()
                    // If sign in fails, display a message to the user.
                    Logger.w("signInWithCredential:failure ${task.exception} error_code =${task.exception}")
                }

            }
    }


    override fun onStop() {
        super.onStop()
        (activity as MainActivity).backFromEditPage()

    }

    companion object {
        private lateinit var auth: FirebaseAuth
        private var currentUser : FirebaseUser ?= null
        private val logInWithPermissionsEmail = "email"
        private val loginWithPermissionProfile = "public_profile"
        val RC_SIGN_IN: Int = 1
    }


}