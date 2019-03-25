package com.zeyad.gadapter.screens.user.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericViewHolder
import kotlinx.android.synthetic.main.section_header_layout.view.*

class SectionHeaderViewHolder(itemView: View) : GenericViewHolder(itemView) {
    override fun <T> bindData(data: T, position: Int, isItemSelected: Boolean, isEnabled: Boolean, isExpanded: Boolean) {
        if (data is String) itemView.sectionHeader.text = data
    }
}
