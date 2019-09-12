package com.terricom.mytype.profile

import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView


class CardLinearSnapHelper : LinearSnapHelper() {
    var mNoNeedToScroll = false

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        return if (mNoNeedToScroll) {
            intArrayOf(0, 0)
        } else {
            super.calculateDistanceToFinalSnap(layoutManager, targetView)
        }
    }

}