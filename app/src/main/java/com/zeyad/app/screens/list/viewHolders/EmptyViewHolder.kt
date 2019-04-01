package com.zeyad.app.screens.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericViewHolder

class EmptyViewHolder(itemView: View) : GenericViewHolder(itemView) {
    override fun <T> bindData(data: T, position: Int, isItemSelected: Boolean, isEnabled: Boolean, isExpanded: Boolean) = Unit
}
