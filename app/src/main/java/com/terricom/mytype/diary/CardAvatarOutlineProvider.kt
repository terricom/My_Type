package com.terricom.mytype.diary

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import com.terricom.mytype.App
import com.terricom.mytype.R

class CardAvatarOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        view.clipToOutline = true
        val radius = App.applicationContext().resources.getDimensionPixelSize(R.dimen._12sdp)
        outline.setRoundRect(0, 0, view.width , view.height, radius.toFloat())
    }
}