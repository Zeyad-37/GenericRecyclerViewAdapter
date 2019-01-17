package com.zeyad.gadapter

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class GenericViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bindData(data: T, isItemSelected: Boolean, position: Int, isEnabled: Boolean)
}
