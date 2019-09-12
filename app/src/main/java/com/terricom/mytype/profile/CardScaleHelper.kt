package com.terricom.mytype.profile

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.App
import com.terricom.mytype.Logger


class CardScaleHelper {
    private var mRecyclerView: RecyclerView? = null
    private var mContext: Context? = null

    private var mScale = 0.9f // 两边视图scale
    private var mPagePadding = 15 // 卡片的padding, 卡片间的距离等于2倍的mPagePadding
    private var mShowLeftCardWidth = 15   // 左边卡片显示大小

    private var mCardWidth: Int = 0 // 卡片宽度
    private var mOnePageWidth: Int = 0 // 滑动一页的距离
    private var mCardGalleryWidth: Int = 0

    var currentItemPos: Int = 0
    private var mCurrentItemOffset: Int = 0

    private val mLinearSnapHelper = CardLinearSnapHelper()

    fun attachToRecyclerView(mRecyclerView: RecyclerView) {
        // 开启log会影响滑动体验, 调试时才开启
//        LogUtils.mLogEnable = false
        this.mRecyclerView = mRecyclerView
        mContext = mRecyclerView.context
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mLinearSnapHelper.mNoNeedToScroll =
                        mCurrentItemOffset == 0 || mCurrentItemOffset == getDestItemOffset(
                            mRecyclerView.adapter!!.itemCount - 1
                        )
                } else {
                    mLinearSnapHelper.mNoNeedToScroll = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // dx>0则表示右滑, dx<0表示左滑, dy<0表示上滑, dy>0表示下滑
                if (dx != 0) {//去掉奇怪的内存疯涨问题
                    mCurrentItemOffset += dx
                    computeCurrentItemPos()
                    Logger.v(
                        String.format(
                            "dx=%s, dy=%s, mScrolledX=%s",
                            dx,
                            dy,
                            mCurrentItemOffset
                        )
                    )
                    onScrolledChangedCallback()
                }
            }
        })

        initWidth()
        mLinearSnapHelper.attachToRecyclerView(mRecyclerView)
    }

    /**
     * 初始化卡片宽度
     */
    private fun initWidth() {
        mRecyclerView!!.post {
            mCardGalleryWidth = mRecyclerView!!.width
            mCardWidth = mCardGalleryWidth - ScreenUtil.dip2px(
                App.applicationContext(),
                2 * (mPagePadding + mShowLeftCardWidth).toFloat()
            )
            mOnePageWidth = mCardWidth
            mRecyclerView!!.smoothScrollToPosition(currentItemPos)
            onScrolledChangedCallback()
        }
    }

    private fun getDestItemOffset(destPos: Int): Int {
        return mOnePageWidth * destPos
    }

    /**
     * 计算mCurrentItemOffset
     */
    private fun computeCurrentItemPos() {
        if (mOnePageWidth <= 0) return
        var pageChanged = false
        // 滑动超过一页说明已翻页
        if (Math.abs(mCurrentItemOffset - currentItemPos * mOnePageWidth) >= mOnePageWidth) {
            pageChanged = true
        }
        if (pageChanged) {
            val tempPos = currentItemPos

            currentItemPos = mCurrentItemOffset / mOnePageWidth
            Logger.d(
                String.format(
                    "=======onCurrentItemPos Changed======= tempPos=%s, mCurrentItemPos=%s",
                    tempPos,
                    currentItemPos
                )
            )
        }
    }

    /**
     * RecyclerView位移事件监听, view大小随位移事件变化
     */
    private fun onScrolledChangedCallback() {
        val offset = mCurrentItemOffset - currentItemPos * mOnePageWidth
        val percent = Math.max(Math.abs(offset) * 1.0 / mOnePageWidth, 0.0001).toFloat()

        Logger.d(String.format("offset=%s, percent=%s", offset, percent))
        var leftView: View? = null
        val currentView: View?
        var rightView: View? = null
        if (currentItemPos > 0) {
            leftView = mRecyclerView!!.layoutManager!!.findViewByPosition(currentItemPos - 1)
        }
        currentView = mRecyclerView!!.layoutManager!!.findViewByPosition(currentItemPos)
        if (currentItemPos < mRecyclerView!!.adapter!!.itemCount - 1) {
            rightView = mRecyclerView!!.layoutManager!!.findViewByPosition(currentItemPos + 1)
        }

        if (leftView != null) {
            // y = (1 - mScale)x + mScale
            leftView!!.setScaleY((1 - mScale) * percent + mScale)
        }
        if (currentView != null) {
            // y = (mScale - 1)x + 1
            currentView!!.setScaleY((mScale - 1) * percent + 1)
        }
        if (rightView != null) {
            // y = (1 - mScale)x + mScale
            rightView!!.setScaleY((1 - mScale) * percent + mScale)
        }
    }

    fun setScale(scale: Float) {
        mScale = scale
    }

    fun setPagePadding(pagePadding: Int) {
        mPagePadding = pagePadding
    }

    fun setShowLeftCardWidth(showLeftCardWidth: Int) {
        mShowLeftCardWidth = showLeftCardWidth
    }
}