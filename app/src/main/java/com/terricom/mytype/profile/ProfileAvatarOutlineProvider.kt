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

class CardAvatarOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        view.clipToOutline = true
        val radius = App.applicationContext().resources.getDimensionPixelSize(R.dimen.elevation_all)
        outline.setRoundRect(0, 0, view.width , view.height, radius.toFloat())
    }
}