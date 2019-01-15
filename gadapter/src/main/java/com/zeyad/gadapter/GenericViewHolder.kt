package com.zeyad.gadapter

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class GenericViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bindData(data: Any, itemSelected: Boolean, position: Int, isEnabled: Boolean)
}
