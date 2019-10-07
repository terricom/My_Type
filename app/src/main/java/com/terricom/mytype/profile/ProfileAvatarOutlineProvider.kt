package com.terricom.mytype.profile

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import com.terricom.mytype.App
import com.terricom.mytype.R

class ProfileAvatarOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        view.clipToOutline = true
        val radius = App.applicationContext().resources.getDimensionPixelSize(R.dimen.radius_profile_avatar)
        outline.setOval(0, 0, radius, radius)
    }
}