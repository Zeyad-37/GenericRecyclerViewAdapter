package com.zeyad.gadapter.fastscroll.viewprovider

import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.zeyad.gadapter.R
import com.zeyad.gadapter.fastscroll.setBackground

class DefaultScrollerViewProvider : ScrollerViewProvider() {

    private lateinit var bubble: View
    private lateinit var handle: View

    override val bubbleOffset: Int
        get() = (if (scroller?.isVertical!!)
            handle.height.toFloat() / 2f - bubble.height
        else
            handle.width.toFloat() / 2f - bubble.width).toInt()

    override fun provideHandleView(container: ViewGroup): View {
        handle = View(context)
        val verticalInset = if (scroller?.isVertical!!) 0 else context.resources.getDimensionPixelSize(R.dimen.fastscroll__handle_inset)
        val horizontalInset = if (!scroller?.isVertical!!) 0 else context.resources.getDimensionPixelSize(R.dimen.fastscroll__handle_inset)
        val handleBg = InsetDrawable(ContextCompat
                .getDrawable(context, R.drawable.fastscroll__default_handle), horizontalInset,
                verticalInset, horizontalInset, verticalInset)
        setBackground(handle, handleBg)
        val handleWidth = context.resources.getDimensionPixelSize(
                if (scroller?.isVertical!!) R.dimen.fastscroll__handle_clickable_width else R.dimen.fastscroll__handle_height)
        val handleHeight = context.resources.getDimensionPixelSize(
                if (scroller?.isVertical!!) R.dimen.fastscroll__handle_height else R.dimen.fastscroll__handle_clickable_width)
        val params = ViewGroup.LayoutParams(handleWidth, handleHeight)
        handle.layoutParams = params
        return handle
    }

    override fun provideBubbleView(container: ViewGroup): View {
        bubble = LayoutInflater.from(context)
                .inflate(R.layout.fastscroll__default_bubble, container, false)
        return bubble
    }

    override fun provideBubbleTextView(): TextView {
        return bubble as TextView
    }

    override fun provideHandleBehavior(): ViewBehavior? {
        return null
    }

    override fun provideBubbleBehavior(): ViewBehavior? {
        return DefaultBubbleBehavior(VisibilityAnimationManager.Builder(bubble).withPivotX(1f).withPivotY(1f).build())
    }
}
