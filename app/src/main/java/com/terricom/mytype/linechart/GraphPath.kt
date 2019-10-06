package com.terricom.mytype.linechart

import android.graphics.Path
import android.graphics.PointF

class GraphPath(width: Int, height: Int, paddingLeft: Int, paddingBottom: Int) : Path() {

    private val mMt: MatrixTranslator = MatrixTranslator(width, height, paddingLeft, paddingBottom)

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(mMt.calcX(x), mMt.calcY(y))
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(mMt.calcX(x), mMt.calcY(y))
    }

}