package com.terricom.mytype.profile

import androidx.lifecycle.ViewModel
import com.terricom.mytype.data.UserManager

class ProfileViewModel: ViewModel() {

    val userName = UserManager.name
    val userPic = UserManager.picture

    val outlineProvider = ProfileAvatarOutlineProvider()

}