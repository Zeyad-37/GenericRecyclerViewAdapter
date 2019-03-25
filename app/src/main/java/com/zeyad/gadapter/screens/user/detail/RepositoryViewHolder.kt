package com.zeyad.gadapter.screens.user.detail

import android.view.View
import com.zeyad.gadapter.GenericViewHolder
import kotlinx.android.synthetic.main.repo_item_layout.*

internal class RepositoryViewHolder(itemView: View) : GenericViewHolder(itemView) {
    override fun <T> bindData(data: T, position: Int, isItemSelected: Boolean, isEnabled: Boolean, isExpanded: Boolean) {
        if (data is Repository) {
            textView_repo_title.text = data.name
        }
    }
}
