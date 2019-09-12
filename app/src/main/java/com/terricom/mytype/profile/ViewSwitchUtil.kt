package com.terricom.mytype.profile

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.widget.ImageView


object ViewSwitchUtils {

    fun startSwitchBackgroundAnim(view: ImageView, bitmap: Bitmap) {
        val oldDrawable = view.getDrawable()
        val oldBitmapDrawable: Drawable
        var oldTransitionDrawable: TransitionDrawable? = null
        if (oldDrawable is TransitionDrawable) {
            oldTransitionDrawable = oldDrawable
            oldBitmapDrawable =
                oldTransitionDrawable!!.findDrawableByLayerId(oldTransitionDrawable.getId(1))
        } else if (oldDrawable is BitmapDrawable) {
            oldBitmapDrawable = oldDrawable
        } else {
            oldBitmapDrawable = ColorDrawable(-0x3d3d3e)
        }

        if (oldTransitionDrawable == null) {
            oldTransitionDrawable =
                TransitionDrawable(arrayOf(oldBitmapDrawable, BitmapDrawable(bitmap)))
            oldTransitionDrawable.setId(0, 0)
            oldTransitionDrawable.setId(1, 1)
            oldTransitionDrawable.isCrossFadeEnabled = true
            view.setImageDrawable(oldTransitionDrawable)
        } else {
            oldTransitionDrawable.setDrawableByLayerId(
                oldTransitionDrawable.getId(0),
                oldBitmapDrawable
            )
            oldTransitionDrawable.setDrawableByLayerId(
                oldTransitionDrawable.getId(1),
                BitmapDrawable(bitmap)
            )
        }
        oldTransitionDrawable.startTransition(1000)
    }
}