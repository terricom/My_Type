package com.terricom.mytype.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.terricom.mytype.MainActivity
import com.terricom.mytype.databinding.FragmentLoginBinding
import kotlinx.android.synthetic.main.activity_main.*

class LoginFragment: Fragment() {

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).toolbar.visibility = View.GONE
        (activity as MainActivity).bottom_nav_view.visibility = View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentLoginBinding.inflate(inflater)

        return binding.root
    }


}