package com.terricom.mytype.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.facebook.login.LoginManager
import com.terricom.mytype.MainActivity
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.activity_main.*

class LoginFragment: Fragment() {

    private val viewModel : LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)}
    private lateinit var binding: FragmentLoginBinding

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).toolbar.visibility = View.GONE
        (activity as MainActivity).bottom_nav_view.visibility = View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.buttonLoginFacebook.setOnClickListener {
            viewModel.loginFB()
            Log.i("getUserProfile", UserManager.userToken)

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile", "user_friends"))

        }



        viewModel.loginFacebook.observe(this, Observer {
            it?.let {
                LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
                viewModel.onLoginFacebookCompleted()
            }
        })

        return binding.root
    }

//    override fun dismiss() {
//        binding.layoutLogin.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_down))
//        Handler().postDelayed({ super.dismiss() }, 200)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        viewModel.callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


}