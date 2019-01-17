package com.zeyad.gadapter.fastscroll.viewprovider

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.zeyad.gadapter.fastscroll.FastScroller

/**
 * Provides [View]s and their behaviors for the handle and bubble of the fastscroller.
 */
abstract class ScrollerViewProvider {

    var scroller: FastScroller? = null
        private set
    private var handleBehavior: ViewBehavior? = null
    private var bubbleBehavior: ViewBehavior? = null

    val context: Context
        get() = scroller!!.context

    /**
     * To offset the position of the bubble relative to the handle. E.g. in [DefaultScrollerViewProvider]
     * the sharp corner of the bubble is aligned with the center of the handle.
     *
     * @return the position of the bubble in relation to the handle (according to the orientation).
     */
    abstract val bubbleOffset: Int

    fun setFastScroller(scroller: FastScroller) {
        this.scroller = scroller
    }

    /**
     * @param container The container [FastScroller] for the view to inflate properly.
     *
     * @return A view which will be by the [FastScroller] used as a handle.
     */
    abstract fun provideHandleView(container: ViewGroup): View

    /**
     * @param container The container [FastScroller] for the view to inflate properly.
     *
     * @return A view which will be by the [FastScroller] used as a bubble.
     */
    abstract fun provideBubbleView(container: ViewGroup): View

    /**
     * Bubble view has to provide a [TextView] that will show the index title.
     *
     * @return A [TextView] that will hold the index title.
     */
    abstract fun provideBubbleTextView(): TextView

    protected abstract fun provideHandleBehavior(): ViewBehavior?

    protected abstract fun provideBubbleBehavior(): ViewBehavior?

    private fun getHandleBehavior(): ViewBehavior? {
        if (handleBehavior == null)
            handleBehavior = provideHandleBehavior()
        return handleBehavior
    }

    private fun getBubbleBehavior(): ViewBehavior? {
        if (bubbleBehavior == null)
            bubbleBehavior = provideBubbleBehavior()
        return bubbleBehavior
    }

    fun onHandleGrabbed() {
        if (getHandleBehavior() != null)
            getHandleBehavior()!!.onHandleGrabbed()
        if (getBubbleBehavior() != null)
            getBubbleBehavior()!!.onHandleGrabbed()
    }

    fun onHandleReleased() {
        if (getHandleBehavior() != null)
            getHandleBehavior()!!.onHandleReleased()
        if (getBubbleBehavior() != null)
            getBubbleBehavior()!!.onHandleReleased()
    }

    fun onScrollStarted() {
        if (getHandleBehavior() != null)
            getHandleBehavior()!!.onScrollStarted()
        if (getBubbleBehavior() != null)
            getBubbleBehavior()!!.onScrollStarted()
    }

    fun onScrollFinished() {
        if (getHandleBehavior() != null)
            getHandleBehavior()!!.onScrollFinished()
        if (getBubbleBehavior() != null)
            getBubbleBehavior()!!.onScrollFinished()
    }
}
