package com.zeyad.gadapter.fastscroll

import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

/**
 * Responsible for updating the handle / bubble position when user scrolls the [android.support.v7.widget.RecyclerView].
 */
class RecyclerViewScrollListener(private val scroller: FastScroller) : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
    private val listeners = ArrayList<ScrollerListener>()
    private var oldScrollState = androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE

    fun addScrollerListener(listener: ScrollerListener) {
        listeners.add(listener)
    }

    override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newScrollState: Int) {
        super.onScrollStateChanged(recyclerView, newScrollState)
        if (newScrollState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE && oldScrollState != androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) {
            scroller.viewProvider?.onScrollFinished()
        } else if (newScrollState != androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE && oldScrollState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) {
            scroller.viewProvider?.onScrollStarted()
        }
        oldScrollState = newScrollState
    }

    override fun onScrolled(rv: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        if (scroller.shouldUpdateHandlePosition()) {
            updateHandlePosition(rv)
        }
    }

    fun updateHandlePosition(rv: androidx.recyclerview.widget.RecyclerView) {
        val relativePos: Float = if (scroller.isVertical) {
            val offset = rv.computeVerticalScrollOffset()
            val extent = rv.computeVerticalScrollExtent()
            val range = rv.computeVerticalScrollRange()
            offset / (range - extent).toFloat()
        } else {
            val offset = rv.computeHorizontalScrollOffset()
            val extent = rv.computeHorizontalScrollExtent()
            val range = rv.computeHorizontalScrollRange()
            offset / (range - extent).toFloat()
        }
        scroller.setScrollerPosition(relativePos)
        notifyListeners(relativePos)
    }

    private fun notifyListeners(relativePos: Float) {
        for (listener in listeners) {
            listener.onScroll(relativePos)
        }
    }

    interface ScrollerListener {
        fun onScroll(relativePos: Float)
    }
}
