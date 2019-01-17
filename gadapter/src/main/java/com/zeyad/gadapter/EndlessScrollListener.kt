package com.zeyad.gadapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log

abstract class EndlessScrollListener : RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    //set visibleThreshold   default: 5
    var visibleThreshold = 5
        private set
    // The current offset index of data you have loaded
    private var currentPage = 0
    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // Sets the starting page index
    //set startingPageIndex   default: 0
    var startingPageIndex = 0
        private set
    // Sets the  footerViewType
    private val defaultNoFooterViewType = -1
    private var footerViewType = -1
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    private val isUseFooterView: Boolean
        get() = footerViewType != defaultNoFooterViewType

    private// get maximum element within the list
    val lastVisibleItemPosition: Int
        get() {
            return when (mLayoutManager) {
                is StaggeredGridLayoutManager -> {
                    val lastVisibleItemPositions = (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                    getLastVisibleItem(lastVisibleItemPositions)
                }
                is LinearLayoutManager -> (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                is GridLayoutManager -> (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
                else -> 0
            }
        }

    constructor(layoutManager: LinearLayoutManager, visibleThreshold: Int) {
        init()
        mLayoutManager = layoutManager
        this.visibleThreshold = visibleThreshold
    }

    constructor(layoutManager: GridLayoutManager) {
        init()
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        init()
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    //init from  self-define
    private fun init() {
        footerViewType = getFooterViewType(defaultNoFooterViewType)
        startingPageIndex = startingPageIndex
        val threshold = visibleThreshold
        if (threshold > visibleThreshold) {
            visibleThreshold = threshold
        }
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        // when dy=0---->list is clear totalItemCount == 0 or init load  previousTotalItemCount=0
        if (dy <= 0) return
        //        Log.i(TAG, "onScrolled-------dy:" + dy);
        val adapter = view.adapter
        val totalItemCount = adapter!!.itemCount
        val lastVisibleItemPosition = lastVisibleItemPosition
        val isAllowLoadMore = lastVisibleItemPosition + visibleThreshold > totalItemCount
        if (isAllowLoadMore) {
            if (isUseFooterView) {
                if (!isFooterView(adapter)) {
                    if (totalItemCount < previousTotalItemCount) {// swipe-refresh reload result to change list size ,reset page index
                        currentPage = startingPageIndex
                        //                            Log.i(TAG, "****totalItemCount:" + totalItemCount + ",previousTotalItemCount:" + previousTotalItemCount + ", currentpage=startingPageIndex");
                    } else if (totalItemCount == previousTotalItemCount) {//if load failure or load empty data , we rollback  page index
                        currentPage = if (currentPage == startingPageIndex) startingPageIndex else --currentPage
                        //                            Log.i(TAG, "!!!!currentpage:" + currentPage);
                    }
                    loading = false
                }
            } else {
                if (totalItemCount > previousTotalItemCount) loading = false
            }
            if (!loading) {
                // If it isn’t currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                // threshold should reflect how many total columns there are too
                previousTotalItemCount = totalItemCount
                currentPage++
                onLoadMore(currentPage, totalItemCount)
                loading = true
                Log.i(TAG, "request pageindex:$currentPage,totalItemsCount:$totalItemCount")
            }
        }
    }

    fun isFooterView(padapter: RecyclerView.Adapter<*>): Boolean {
        var isFooterView = false
        val ptotalItemCount = padapter.itemCount
        if (ptotalItemCount > 0) {
            val lastPosition = ptotalItemCount - 1
            val lastViewType = padapter.getItemViewType(lastPosition)
            //  check the lastview is footview
            isFooterView = lastViewType == footerViewType
        }
        //        Log.i(TAG, "isFooterView:" + isFooterView);
        return isFooterView
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    // set FooterView type
    // if don't use footview load more  default: -1
    abstract fun getFooterViewType(defaultNoFooterViewType: Int): Int

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(page: Int, totalItemsCount: Int)

    companion object {
        private const val TAG = "scroll-listener"
    }
}
