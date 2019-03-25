package com.zeyad.gadapter

import android.support.v7.widget.RecyclerView

interface OnItemClickListener {
    fun onItemClicked(position: Int, itemInfo: ItemInfo, holder: GenericViewHolder<*>)
}

interface OnItemLongClickListener {
    fun onItemLongClicked(position: Int, itemInfo: ItemInfo, holder: GenericViewHolder<*>): Boolean
}

interface OnSwipeListener {
    fun onItemSwipe(itemInfo: ItemInfo)
}

interface OnExpandListener {
    fun expand(isExpanded: Boolean)
}

interface OnStartDragListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}
