package com.lge.support.second.application.view.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class SwipeLockableViewPager(context: Context, attrs: AttributeSet): ViewPager(context, attrs) {
    private var swipeEnabled = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (swipeEnabled) {
            true -> super.onTouchEvent(event)
            false -> false
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return when (swipeEnabled) {
            true -> super.onInterceptTouchEvent(event)
            false -> false
        }
    }

    fun setSwipePagingEnabled(swipeEnabled: Boolean) {
        //기본은 false(swipe x) 혹시 swipe값 주고 싶으면 이 함수 호출, 인자는 true
        this.swipeEnabled = swipeEnabled
    }
}