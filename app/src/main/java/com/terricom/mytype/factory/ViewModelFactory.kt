package com.terricom.mytype.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.terricom.mytype.achievement.AchievementViewModel
import com.terricom.mytype.data.FirebaseRepository
import com.terricom.mytype.diary.DiaryViewModel
import com.terricom.mytype.foodie.FoodieViewModel
import com.terricom.mytype.login.LoginViewModel
import com.terricom.mytype.profile.ProfileViewModel
import com.terricom.mytype.query.QueryViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DiaryViewModel::class.java) ->
                    DiaryViewModel(firebaseRepository)

                isAssignableFrom(AchievementViewModel::class.java) ->
                    AchievementViewModel(firebaseRepository)

                isAssignableFrom(FoodieViewModel::class.java) ->
                    FoodieViewModel(firebaseRepository)

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(firebaseRepository)

                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(firebaseRepository)

                isAssignableFrom(QueryViewModel::class.java) ->
                    QueryViewModel(firebaseRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
