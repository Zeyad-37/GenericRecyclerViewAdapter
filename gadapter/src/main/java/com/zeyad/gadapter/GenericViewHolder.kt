package com.zeyad.gadapter

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

abstract class GenericViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    abstract fun bindData(data: T, isItemSelected: Boolean, position: Int, isEnabled: Boolean)
}
