package com.terricom.mytype.foodie

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.MainActivity
import com.terricom.mytype.NavigationDirections
import com.terricom.mytype.R
import kotlinx.android.synthetic.main.activity_main.*

class ReferenceDialog: AppCompatDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({ this.dismiss()
            findNavController().navigate(NavigationDirections.navigateToFoodieFragment())
            (activity as MainActivity).toolbar.visibility = View.VISIBLE}, 3000)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = com.terricom.mytype.databinding.DialogReferenceBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.dialog = this


        return binding.root
    }



}