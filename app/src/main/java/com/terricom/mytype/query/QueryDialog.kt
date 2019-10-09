package com.terricom.mytype.query

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.terricom.mytype.R

class QueryDialog: AppCompatDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = com.terricom.mytype.databinding.DialogQueryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.dialog = this
        this.isCancelable = true
        return binding.root
    }


}