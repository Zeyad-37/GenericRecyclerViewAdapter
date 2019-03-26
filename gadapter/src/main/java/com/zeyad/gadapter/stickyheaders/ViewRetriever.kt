package com.zeyad.gadapter.stickyheaders

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

internal interface ViewRetriever {

    fun getViewHolderForPosition(headerPositionToShow: Int): RecyclerView.ViewHolder?

    class RecyclerViewRetriever internal constructor(private val recyclerView: RecyclerView) : ViewRetriever {

        private lateinit var currentViewHolder: RecyclerView.ViewHolder
        private var currentViewType: Int = 0

        init {
            this.currentViewType = -1
        }

        override fun getViewHolderForPosition(headerPositionToShow: Int): RecyclerView.ViewHolder? {
            recyclerView.adapter?.let {
                if (currentViewType != it.getItemViewType(headerPositionToShow)) {
                    currentViewType = it.getItemViewType(headerPositionToShow)
                    currentViewHolder = it.createViewHolder(recyclerView.parent as ViewGroup, currentViewType)
                }
                return currentViewHolder
            } ?: run { return null }
        }
    }
}
