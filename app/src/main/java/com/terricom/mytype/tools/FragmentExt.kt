package com.terricom.mytype.tools

import androidx.fragment.app.Fragment
import com.terricom.mytype.App
import com.terricom.mytype.factory.ViewModelFactory

fun Fragment.getVmFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as App).firebaseRepository
    return ViewModelFactory(repository)
}
