package com.terricom.mytype.calendar

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.App
import com.terricom.mytype.R

class SpaceItemDecoration(
    private var spacing: Int,
    private var isBound: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (isBound) {
            outRect.set(spacing, spacing, spacing, spacing)
        } else {
            outRect.set(spacing, App.applicationContext().resources.getDimension(R.dimen.standard_0).toInt()
                , spacing, App.applicationContext().resources.getDimension(R.dimen.standard_0).toInt())

        }
    }
}