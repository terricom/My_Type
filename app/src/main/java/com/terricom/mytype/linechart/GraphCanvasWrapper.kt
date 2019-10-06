package com.terricom.mytype.linechart

import android.graphics.Canvas
import android.graphics.Paint

class GraphCanvasWrapper(
    val canvas: Canvas?,
    width: Int,
    height: Int,
    paddingLeft: Int,
    paddingBottom: Int
) {

    private val mMt: MatrixTranslator = MatrixTranslator(width, height, paddingLeft, paddingBottom)

    fun drawCircle(cx: Float, cy: Float, radius: Float, paint: Paint) {
        canvas!!.drawCircle(mMt.calcX(cx), mMt.calcY(cy), radius, paint)
    }

    fun drawLine(startX: Float, startY: Float, stopX: Float, stopY: Float, paint: Paint) {
        canvas!!.drawLine(
            mMt.calcX(startX),
            mMt.calcY(startY),
            mMt.calcX(stopX),
            mMt.calcY(stopY),
            paint
        )
    }

    fun drawText(
        text: String,
        x: Float,
        y: Float,
        paint: Paint,
        degree: Float,
        px: Float,
        py: Float
    ) {
        canvas!!.save()
        canvas.rotate(degree, mMt.calcX(px), mMt.calcY(py))
        canvas.drawText(text, mMt.calcX(x), mMt.calcY(y), paint)
        canvas.restore()
    }

}