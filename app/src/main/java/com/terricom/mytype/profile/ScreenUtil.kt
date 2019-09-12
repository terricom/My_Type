package com.terricom.mytype.profile

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.WindowManager


class ScreenUtil private constructor() {
    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {

        /**
         * 获得屏幕高度,单位是px
         *
         * @param context
         * @return
         */
        fun getScreenWidth(context: Context): Int {
            val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(outMetrics)
            return outMetrics.widthPixels
        }

        /**
         * 获得屏幕宽度，单位是px
         *
         * @param context
         * @return
         */
        fun getScreenHeight(context: Context): Int {
            val wm = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val outMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(outMetrics)
            return outMetrics.heightPixels
        }

        /**
         * 获得状态栏的高度
         *
         * @param context
         * @return
         */
        fun getStatusHeight(context: Context): Int {

            var statusHeight = -1
            try {
                val clazz = Class.forName("com.android.internal.R\$dimen")
                val `object` = clazz.newInstance()
                val height = Integer.parseInt(
                    clazz.getField("status_bar_height")
                        .get(`object`).toString()
                )
                statusHeight = context.getResources().getDimensionPixelSize(height)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return statusHeight
        }

        /**
         * 获取当前屏幕截图，包含状态栏
         *
         * @param activity
         * @return
         */
        fun snapShotWithStatusBar(activity: Activity): Bitmap? {
            val view = activity.window.decorView
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bmp = view.drawingCache
            val width = getScreenWidth(activity)
            val height = getScreenHeight(activity)
            var bp: Bitmap? = null
            bp = Bitmap.createBitmap(bmp, 0, 0, width, height)
            view.destroyDrawingCache()
            return bp

        }

        /**
         * 获取当前屏幕截图，不包含状态栏
         *
         * @param activity
         * @return
         */
        fun snapShotWithoutStatusBar(activity: Activity): Bitmap? {
            val view = activity.window.decorView
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bmp = view.drawingCache
            val frame = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(frame)
            val statusBarHeight = frame.top

            val width = getScreenWidth(activity)
            val height = getScreenHeight(activity)
            var bp: Bitmap? = null
            bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight)
            view.destroyDrawingCache()
            return bp

        }

        /**
         * 将px值转换为dip或dp值，保证尺寸大小不变
         *
         * @param pxValue （DisplayMetrics类中属性density）
         * @return
         */
        fun px2dip(context: Context, pxValue: Float): Int {
            val scale = context.getResources().getDisplayMetrics().density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * 将dip或dp值转换为px值，保证尺寸大小不变
         *
         * @param dipValue （DisplayMetrics类中属性density）
         * @return
         */
        fun dip2px(context: Context, dipValue: Float): Int {
            val scale = context.getResources().getDisplayMetrics().density
            return (dipValue * scale + 0.5f).toInt()
        }

        /**
         * sp转换成px
         *
         * @param context Context
         * @param sp      sp
         * @return px值
         */
        fun sp2px(context: Context, sp: Float): Float {
            val scale = context.getResources().getDisplayMetrics().scaledDensity
            return sp * scale
        }


        private var sNoncompatDensity: Float = 0.toFloat()
        private var sNoncompatScaledDensity: Float = 0.toFloat()

        fun setCustomDensity(activity: Activity, application: Application) {
            val appDisplayMetrics = application.getResources().getDisplayMetrics()
            if (sNoncompatDensity == 0f) {
                sNoncompatDensity = appDisplayMetrics.density
                sNoncompatScaledDensity = appDisplayMetrics.scaledDensity
                application.registerComponentCallbacks(object : ComponentCallbacks {
                    override fun onConfigurationChanged(newConfig: Configuration?) {
                        if (newConfig != null && newConfig!!.fontScale > 0) {
                            sNoncompatScaledDensity =
                                application.getResources().getDisplayMetrics().scaledDensity
                        }
                    }

                    override fun onLowMemory() {

                    }
                })
            }

            val targetDensity = (appDisplayMetrics.heightPixels / 640).toFloat()
            val targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity)
            val targetDensityDpi = (160 * targetDensity).toInt()

            appDisplayMetrics.density = targetDensity
            appDisplayMetrics.scaledDensity = targetScaledDensity
            appDisplayMetrics.densityDpi = targetDensityDpi

            val activityDisplayMetrics = activity.resources.displayMetrics
            activityDisplayMetrics.density = targetDensity
            activityDisplayMetrics.scaledDensity = targetScaledDensity
            activityDisplayMetrics.densityDpi = targetDensityDpi
        }
    }


}