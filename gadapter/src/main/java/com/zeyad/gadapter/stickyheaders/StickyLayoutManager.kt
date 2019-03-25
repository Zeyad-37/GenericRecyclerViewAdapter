package com.zeyad.gadapter.stickyheaders

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.zeyad.gadapter.GenericAdapter.Companion.SECTION_HEADER
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderListener
import java.util.*

open class StickyLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean, headerHandler: StickyHeaderHandler) : LinearLayoutManager(context, orientation, reverseLayout) {

    private var positioner: StickyHeaderPositioner? = null
    private var headerHandler: StickyHeaderHandler? = null
    private var viewRetriever: ViewRetriever.RecyclerViewRetriever? = null
    private var headerElevation = StickyHeaderPositioner.NO_ELEVATION
    private var listener: StickyHeaderListener? = null
    private val headerPositions: MutableList<Int> = mutableListOf()

    private val visibleHeaders: Map<Int, View>
        get() {
            val visibleHeaders = LinkedHashMap<Int, View>()

            for (i in 0 until childCount) {
                val view = getChildAt(i)
                val dataPosition = getPosition(view!!)
                if (headerPositions.contains(dataPosition)) {
                    visibleHeaders[dataPosition] = view
                }
            }
            return visibleHeaders
        }

    constructor(context: Context, headerHandler: StickyHeaderHandler) : this(context, LinearLayoutManager.VERTICAL, false, headerHandler) {
        init(headerHandler)
    }

    init {
        init(headerHandler)
    }

    private fun init(stickyHeaderHandler: StickyHeaderHandler) {
        setStickyHeaderHandler(stickyHeaderHandler)
    }

    /**
     * Enable or disable elevation for Sticky Headers.
     *
     *
     * If you want to specify a specific amount of elevation, use
     * [StickyLayoutManager.elevateHeaders]
     *
     * @param elevateHeaders Enable Sticky Header elevation. Default is false.
     */
    fun elevateHeaders(elevateHeaders: Boolean) {
        this.headerElevation = if (elevateHeaders)
            StickyHeaderPositioner.DEFAULT_ELEVATION
        else
            StickyHeaderPositioner.NO_ELEVATION
        elevateHeaders(headerElevation)
    }

    /**
     * Enable Sticky Header elevation with a specific amount.
     *
     * @param dp elevation in dp
     */
    fun elevateHeaders(dp: Int) {
        this.headerElevation = dp
        if (positioner != null) {
            positioner?.setElevateHeaders(dp)
        }
    }

    private fun setStickyHeaderHandler(headerHandler: StickyHeaderHandler) {
        Preconditions.checkNotNull<Any>(headerHandler, "StickyHeaderHandler == null")
        this.headerHandler = headerHandler
        headerPositions.clear()
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        cacheHeaderPositions()
        positioner?.reset(orientation, findFirstVisibleItemPosition())
        positioner?.updateHeaderState(
                findFirstVisibleItemPosition(), visibleHeaders, viewRetriever!!)
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val scroll = super.scrollHorizontallyBy(dx, recycler, state)
        if (Math.abs(scroll) > 0) {
            positioner?.updateHeaderState(
                    findFirstVisibleItemPosition(), visibleHeaders, viewRetriever!!)
        }
        return scroll
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val scroll = super.scrollVerticallyBy(dy, recycler, state)
        if (Math.abs(scroll) > 0) {
            positioner?.updateHeaderState(
                    findFirstVisibleItemPosition(), visibleHeaders, viewRetriever!!)
        }
        return scroll
    }

    private fun cacheHeaderPositions() {
        headerPositions.clear()
        val adapterData = headerHandler?.adapterData
        if (adapterData == null) {
            positioner?.setHeaderPositions(headerPositions)
            return
        }
        for (i in adapterData.indices) {
            if (adapterData[i].id == SECTION_HEADER.toLong() || adapterData[i].layoutId == SECTION_HEADER) {
                headerPositions.add(i)
            }
        }
        positioner?.setHeaderPositions(headerPositions)
    }

    /**
     * Register a callback to be invoked when a header is attached/re-bound or detached.
     *
     * @param listener The callback that will be invoked, or null to unset.
     */
    fun setStickyHeaderListener(listener: StickyHeaderListener?) {
        this.listener = listener
        positioner?.setListener(listener)
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        Preconditions.validateParentView(view!!)
        viewRetriever = ViewRetriever.RecyclerViewRetriever(view)
        positioner = StickyHeaderPositioner(view)
        positioner?.setElevateHeaders(headerElevation)
        positioner?.setListener(listener)
    }

    override fun removeAndRecycleAllViews(recycler: RecyclerView.Recycler) {
        super.removeAndRecycleAllViews(recycler)
        positioner?.clearHeader()
    }
}
