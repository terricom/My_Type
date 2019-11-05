package com.terricom.mytype.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.terricom.mytype.achievement.AchievementViewModel
import com.terricom.mytype.data.source.MyTypeRepository
import com.terricom.mytype.diary.DiaryViewModel
import com.terricom.mytype.foodie.FoodieViewModel
import com.terricom.mytype.login.LoginViewModel
import com.terricom.mytype.profile.ProfileViewModel
import com.terricom.mytype.query.QueryViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val myTypeRepository: MyTypeRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(DiaryViewModel::class.java) ->
                    DiaryViewModel(myTypeRepository)

                isAssignableFrom(AchievementViewModel::class.java) ->
                    AchievementViewModel(myTypeRepository)

                isAssignableFrom(FoodieViewModel::class.java) ->
                    FoodieViewModel(myTypeRepository)

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(myTypeRepository)

                isAssignableFrom(ProfileViewModel::class.java) ->
                    ProfileViewModel(myTypeRepository)

                isAssignableFrom(QueryViewModel::class.java) ->
                    QueryViewModel(myTypeRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
