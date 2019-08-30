package com.terricom.mytype.foodie

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FoodieViewModel: ViewModel() {
    val addFood = MutableLiveData<String>()

}