package com.zeyad.app.screens.detail

import android.view.View
import com.zeyad.gadapter.GenericViewHolder

import kotlinx.android.synthetic.main.repo_item_layout.view.*

internal class RepositoryViewHolder(itemView: View) : GenericViewHolder(itemView) {
    override fun <T> bindData(repository: T, position: Int, isItemSelected: Boolean, isEnabled: Boolean, isExpanded: Boolean) {
        itemView.textView_repo_title.text = (repository as Repository).name
    }
}
