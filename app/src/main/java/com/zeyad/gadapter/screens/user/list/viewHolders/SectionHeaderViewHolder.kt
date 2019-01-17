package com.zeyad.gadapter.screens.user.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericViewHolder
import kotlinx.android.synthetic.main.section_header_layout.view.*

class SectionHeaderViewHolder(itemView: View) : GenericViewHolder<String>(itemView) {
    override fun bindData(title: String, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {
        itemView.sectionHeader.text = title
    }
}
