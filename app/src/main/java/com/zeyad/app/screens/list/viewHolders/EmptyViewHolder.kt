package com.zeyad.app.screens.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericViewHolder

class EmptyViewHolder(itemView: View) : GenericViewHolder<Unit>(itemView) {
    override fun bindData(data: Unit, position: Int, isItemSelected: Boolean, isEnabled: Boolean, isExpanded: Boolean) = Unit
}
