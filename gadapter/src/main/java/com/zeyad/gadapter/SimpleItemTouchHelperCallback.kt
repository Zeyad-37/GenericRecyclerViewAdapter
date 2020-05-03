package com.zeyad.gadapter

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper

class SimpleItemTouchHelperCallback(private val mAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder): Int {
        // Set movement flags based on the layout manager
        return when {
            recyclerView.layoutManager is androidx.recyclerview.widget.GridLayoutManager -> {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags = 0
                ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
            }
            recyclerView.layoutManager is androidx.recyclerview.widget.StaggeredGridLayoutManager -> {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                val swipeFlags = 0
                ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
            }
            else -> {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
            }
        }
    }

    override fun onMove(recyclerView: androidx.recyclerview.widget.RecyclerView, source: androidx.recyclerview.widget.RecyclerView.ViewHolder, target: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
        return if (source.itemViewType != target.itemViewType) {
            false
        } else mAdapter.onItemMove(source.adapterPosition, target.adapterPosition)
        // Notify the adapter of the move
        //        return true;
    }

    override fun isLongPressDragEnabled(): Boolean = true

    override fun isItemViewSwipeEnabled(): Boolean = true

    override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, i: Int) {
        // Notify the adapter of the dismissal
        mAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onSelectedChanged(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, actionState: Int) {
        // We only want the active item to change
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is ItemTouchHelperViewHolder) {
                // Let the view holder know that this item is being moved or dragged
                val itemViewHolder = viewHolder as ItemTouchHelperViewHolder?
                itemViewHolder?.onItemSelected()
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = ALPHA_FULL
        if (viewHolder is ItemTouchHelperViewHolder) {
            // Tell the view holder it's time to restore the idle state
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemClear()
        }
    }

    override fun onChildDraw(c: Canvas,
                             recyclerView: androidx.recyclerview.widget.RecyclerView,
                             viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                             dX: Float,
                             dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            viewHolder.itemView.alpha = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.width.toFloat()
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    companion object {

        const val ALPHA_FULL = 1.0f
    }
}
