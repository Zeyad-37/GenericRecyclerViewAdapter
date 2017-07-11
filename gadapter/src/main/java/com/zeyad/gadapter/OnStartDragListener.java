package com.zeyad.gadapter;

import android.support.v7.widget.RecyclerView;

/**
 * @author by ZIaDo on 7/9/17.
 */
public interface OnStartDragListener {
    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);
}
