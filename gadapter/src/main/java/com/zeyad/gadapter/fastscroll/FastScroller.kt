package com.zeyad.gadapter.fastscroll

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.zeyad.gadapter.GenericListAdapter
import com.zeyad.gadapter.GenericRecyclerViewAdapter
import com.zeyad.gadapter.R
import com.zeyad.gadapter.fastscroll.viewprovider.DefaultScrollerViewProvider
import com.zeyad.gadapter.fastscroll.viewprovider.ScrollerViewProvider

class FastScroller @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {
    private val scrollListener = RecyclerViewScrollListener(this)
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView

    private lateinit var bubble: View
    private lateinit var handle: View
    private lateinit var bubbleTextView: TextView

    private var bubbleOffset: Int = 0
    private var handleColor: Int = 0
    private var bubbleColor: Int = 0
    private var bubbleTextAppearance: Int = 0
    private var scrollerOrientation: Int = 0

    //TODO the name should be fixed, also check if there is a better way of handling the visibility, because this is somewhat convoluted
    private var maxVisibility: Int = 0

    private var manuallyChangingPosition: Boolean = false

    /**
     * Enables custom layout for [FastScroller].
     *
     * @param viewProvider A [ScrollerViewProvider] for the [FastScroller] to use when building layout.
     */
    internal var viewProvider: ScrollerViewProvider? = null
        set(viewProvider) {
            removeAllViews()
            field = viewProvider
            viewProvider?.setFastScroller(this)
            bubble = viewProvider?.provideBubbleView(this)!!
            handle = viewProvider.provideHandleView(this)
            bubbleTextView = viewProvider.provideBubbleTextView()
            addView(bubble)
            addView(handle)
        }
    private lateinit var titleProvider: SectionTitleProvider

    private val isRecyclerViewNotScrollable: Boolean
        get() = if (isVertical) {
            recyclerView.getChildAt(0).height * recyclerView.adapter?.itemCount!! <= recyclerView.height
        } else {
            recyclerView.getChildAt(0).width * recyclerView.adapter?.itemCount!! <= recyclerView.width
        }

    val isVertical: Boolean
        get() = scrollerOrientation == LinearLayout.VERTICAL

    init {
        clipChildren = false
        val style = context
                .obtainStyledAttributes(attrs, R.styleable.fastscroll__fastScroller,
                        R.attr.fastscroll__style, 0)
        try {
            bubbleColor = style
                    .getColor(R.styleable.fastscroll__fastScroller_fastscroll__bubbleColor, STYLE_NONE)
            handleColor = style
                    .getColor(R.styleable.fastscroll__fastScroller_fastscroll__handleColor, STYLE_NONE)
            bubbleTextAppearance = style
                    .getResourceId(R.styleable.fastscroll__fastScroller_fastscroll__bubbleTextAppearance, STYLE_NONE)
        } finally {
            style.recycle()
        }
        maxVisibility = visibility
        viewProvider = DefaultScrollerViewProvider()
    }

    /**
     * Attach the [FastScroller] to [RecyclerView]. Should be used after the adapter is set
     * to the [RecyclerView]. If the adapter implements SectionTitleProvider, the FastScroller
     * will show a bubble with title.
     *
     * @param recyclerView A [RecyclerView] to attach the [FastScroller] to.
     */
    fun setRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        this.recyclerView = recyclerView
        val adapter = recyclerView.adapter
        if (adapter is GenericRecyclerViewAdapter)
            adapter.sectionTitleProvider?.let {
                titleProvider = it
            }
        else if (adapter is GenericListAdapter) {
            adapter.sectionTitleProvider?.let {
                titleProvider = it
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
        invalidateVisibility()
        recyclerView.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                invalidateVisibility()
            }

            override fun onChildViewRemoved(parent: View, child: View) {
                invalidateVisibility()
            }
        })
    }

    /**
     * Set the background color of the bubble.
     *
     * @param color Color in hex notation with alpha channel, e.g. 0xFFFFFFFF
     */
    fun setBubbleColor(color: Int) {
        bubbleColor = color
        invalidate()
    }

    /**
     * Set the background color of the handle.
     *
     * @param color Color in hex notation with alpha channel, e.g. 0xFFFFFFFF
     */
    fun setHandleColor(color: Int) {
        handleColor = color
        invalidate()
    }

    /**
     * Sets the text appearance of the bubble.
     *
     * @param textAppearanceResourceId The id of the resource to be used as text appearance of the bubble.
     */
    fun setBubbleTextAppearance(textAppearanceResourceId: Int) {
        bubbleTextAppearance = textAppearanceResourceId
        invalidate()
    }

    /**
     * Add a [RecyclerViewScrollListener.ScrollerListener]
     * to be notified of user scrolling
     *
     * @param listener
     */
    fun addScrollerListener(listener: RecyclerViewScrollListener.ScrollerListener) {
        scrollListener.addScrollerListener(listener)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        initHandleMovement()
        bubbleOffset = viewProvider?.bubbleOffset!!
        applyStyling() //TODO this doesn't belong here, even if it works
        if (!isInEditMode) {
            //sometimes recycler starts with a defined scroll (e.g. when coming from saved state)
            scrollListener.updateHandlePosition(recyclerView)
        }
    }

    /**
     * Set the orientation of the [FastScroller]. The orientation of the [FastScroller]
     * should generally match the orientation of connected  [RecyclerView] for good UX but it's not enforced.
     * Note: This method is overridden from [LinearLayout.setOrientation] but for [FastScroller]
     * it has a totally different meaning.
     *
     * @param orientation of the [FastScroller]. [.VERTICAL] or [.HORIZONTAL]
     */
    override fun setOrientation(orientation: Int) {
        scrollerOrientation = orientation
        //switching orientation, because orientation in linear layout
        //is something different than orientation of fast scroller
        super.setOrientation(if (orientation == LinearLayout.HORIZONTAL) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL)
    }

    private fun applyStyling() {
        if (bubbleColor != STYLE_NONE)
            setBackgroundTint(bubbleTextView, bubbleColor)
        if (handleColor != STYLE_NONE)
            setBackgroundTint(handle, handleColor)
        if (bubbleTextAppearance != STYLE_NONE)
            TextViewCompat.setTextAppearance(bubbleTextView, bubbleTextAppearance)
    }

    private fun setBackgroundTint(view: View, color: Int) {
        val background = DrawableCompat.wrap(view.background)
        DrawableCompat.setTint(background.mutate(), color)
        setBackground(view, background)
    }

    private fun initHandleMovement() {
        handle.setOnTouchListener(OnTouchListener { _, event ->
            requestDisallowInterceptTouchEvent(true)
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                if (event.action == MotionEvent.ACTION_DOWN)
                    viewProvider?.onHandleGrabbed()
                manuallyChangingPosition = true
                val relativePos = getRelativeTouchPosition(event)
                setScrollerPosition(relativePos)
                setRecyclerViewPosition(relativePos)
                return@OnTouchListener true
            } else if (event.action == MotionEvent.ACTION_UP) {
                manuallyChangingPosition = false
                viewProvider?.onHandleReleased()
                return@OnTouchListener true
            }
            false
        })
    }

    private fun getRelativeTouchPosition(event: MotionEvent): Float {
        return if (isVertical) {
            val yInParent = event.rawY - getViewRawY(handle)
            yInParent / (height - handle.height)
        } else {
            val xInParent = event.rawX - getViewRawX(handle)
            xInParent / (width - handle.width)
        }
    }

    override fun setVisibility(visibility: Int) {
        maxVisibility = visibility
        invalidateVisibility()
    }

    private fun invalidateVisibility() {
        if (recyclerView.adapter == null ||
                recyclerView.adapter?.itemCount == 0 ||
                recyclerView.getChildAt(0) == null ||
                isRecyclerViewNotScrollable ||
                maxVisibility != View.VISIBLE) {
            super.setVisibility(View.INVISIBLE)
        } else {
            super.setVisibility(View.VISIBLE)
        }
    }

    private fun setRecyclerViewPosition(relativePos: Float) {
        if (recyclerView.adapter == null) {
            return
        }
        val itemCount = recyclerView.adapter?.itemCount
        val targetPos = getValueInRange(0f, (itemCount!! - 1).toFloat(),
                (relativePos * itemCount.toFloat()).toInt().toFloat()).toInt()
        recyclerView.scrollToPosition(targetPos)
        bubbleTextView.text = titleProvider.getSectionTitle(targetPos)
    }

    internal fun setScrollerPosition(relativePos: Float) {
        if (isVertical) {
            bubble.y = getValueInRange(0f,
                    (height - bubble.height).toFloat(),
                    relativePos * (height - handle.height) + bubbleOffset)
            handle.y = getValueInRange(
                    0f,
                    (height - handle.height).toFloat(),
                    relativePos * (height - handle.height))
        } else {
            bubble.x = getValueInRange(
                    0f,
                    (width - bubble.width).toFloat(),
                    relativePos * (width - handle.width) + bubbleOffset)
            handle.x = getValueInRange(
                    0f,
                    (width - handle.width).toFloat(),
                    relativePos * (width - handle.width))
        }
    }

    internal fun shouldUpdateHandlePosition(): Boolean {
        return !manuallyChangingPosition && recyclerView.childCount > 0
    }

    companion object {

        private const val STYLE_NONE = -1
    }
}
