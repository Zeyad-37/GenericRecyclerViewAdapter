package com.zeyad.gadapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class GenericViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View?
        get() = itemView

    abstract fun bindData(data: T, position: Int, isItemSelected: Boolean, isEnabled: Boolean)
}
