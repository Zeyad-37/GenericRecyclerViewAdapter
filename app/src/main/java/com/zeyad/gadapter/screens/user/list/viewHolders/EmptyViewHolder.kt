package com.zeyad.gadapter.screens.user.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericViewHolder

class EmptyViewHolder(itemView: View) : GenericViewHolder<Any>(itemView) {
    override fun bindData(data: Any, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {}
}
