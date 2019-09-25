package com.terricom.mytype.tools

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


class DrawEdxtView : EditText, OnTouchListener {

    private var mContext: Context? = null

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    private var lastX: Int = 0
    private var lastY: Int = 0

    private var downX: Int = 0
    private var downY: Int = 0 // 按下View的X，Y坐标
    private var upX: Int = 0
    private var upY: Int = 0 // 放手View的X,Y坐标
    private var rangeDifferenceX: Int = 0
    private var rangeDifferenceY: Int = 0 // 放手和按下X,Y值差
    private val mDistance = 10 // 设定点击事件的移动距离值
    private var mL: Int = 0
    private var mB: Int = 0
    private var mR: Int = 0
    private var mT: Int = 0//重绘时layout的值
    private fun getDisplayMetrics() {
        val dm = resources.displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels - 50
    }

    constructor(context: Context) : super(context) {
        mContext = context
        getDisplayMetrics()
        setOnTouchListener(this)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        getDisplayMetrics()
        setOnTouchListener(this)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        mContext = context
        getDisplayMetrics()
        setOnTouchListener(this)
    }

    interface IOnKeyboardStateChangedListener {
        fun openKeyboard()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX.toInt()
                downY = event.rawY.toInt()

                lastX = event.rawX.toInt()// 获取触摸事件触摸位置的原始X坐标
                lastY = event.rawY.toInt()

                Logger.d("按下: $downX----X轴坐标")
                Logger.d("按下：downY----Y轴坐标")
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.rawX.toInt() - lastX
                val dy = event.rawY.toInt() - lastY

                mL = v.getLeft() + dx
                mB = v.getBottom() + dy
                mR = v.getRight() + dx
                mT = v.getTop() + dy

                if (mL < 0) {
                    mL = 0
                    mR = mL + v.getWidth()
                }

                if (mT < 0) {
                    mT = 0
                    mB = mT + v.getHeight()
                }

                if (mR > screenWidth) {
                    mR = screenWidth
                    mL = mR - v.getWidth()
                }

                if (mB > screenHeight) {
                    mB = screenHeight
                    mT = mB - v.getHeight()
                }
                v.layout(mL, mT, mR, mB)
                Logger.d("绘制：l=$mL;t=$mT;r=$mR;b=$mB")

                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
                v.postInvalidate()

                v.setFocusable(false)
                v.setFocusableInTouchMode(false)
                hideSoftInput(
                    (mContext as Activity?)!!,
                    v
                )
            }
            MotionEvent.ACTION_UP -> {
                upX = event.rawX.toInt()
                upY = event.rawY.toInt()

                Logger.d("离开：$upX----X轴坐标")
                Logger.d("离开：$upY----Y轴坐标")

                rangeDifferenceX = upX - downX
                rangeDifferenceY = upY - downY
                if (rangeDifferenceX > 0 && rangeDifferenceX <= mDistance) {
                    if (rangeDifferenceY >= 0 && rangeDifferenceY <= mDistance) {
                        v.setFocusable(true)
                        v.setFocusableInTouchMode(true)
                        Logger.d("是否是点击事件： true.toString() + ")

                    } else {
                        if (rangeDifferenceY <= 0 && rangeDifferenceY >= -mDistance) {
                            v.setFocusable(true)
                            v.setFocusableInTouchMode(true)
                            Logger.d("是否是点击事件：true.toString() + ")

                        } else {
                            v.setFocusable(false)
                            v.setFocusableInTouchMode(false)
                            Logger.d("是否是点击事件：false.toString() + ")
                        }
                    }
                } else {
                    if (rangeDifferenceX <= 0 && rangeDifferenceX >= -mDistance) {
                        v.setFocusable(true)
                        v.setFocusableInTouchMode(true)
                        Logger.d("是否是点击事件：true.toString() + ")

                    } else {
                        v.setFocusable(false)
                        v.setFocusableInTouchMode(false)
                        Logger.d("是否是点击事件： false.toString() + ")
                    }
                }
            }
            else -> {
            }
        }
        return false
    }

    companion object {

        /*隐藏键盘*/
        fun hideSoftInput(mContext: Activity, view: View) {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        }

        /*弹出键盘*/
        fun showSoftInput(mContext: Activity, view: View) {
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isActive()) {
                imm.showSoftInput(view, 0)
            }
        }
    }

}