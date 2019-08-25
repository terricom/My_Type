package com.terricom.mytype

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    // Record current fragment to support data binding
    val currentFragmentType = MutableLiveData<CurrentFragmentType>()

    // According to current fragment to change different drawer toggle

    // According to current fragment to change different drawer toggle
    val currentDrawerToggleType: LiveData<DrawerToggleType> = Transformations.map(currentFragmentType) {
        when (it) {
            CurrentFragmentType.DREAMBOARD -> DrawerToggleType.BACK
            else -> DrawerToggleType.NORMAL
        }
    }

}