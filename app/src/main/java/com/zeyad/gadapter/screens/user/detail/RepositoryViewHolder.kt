package com.zeyad.gadapter.screens.user.detail

import android.view.View
import com.zeyad.gadapter.GenericViewHolder
import kotlinx.android.synthetic.main.repo_item_layout.*

internal class RepositoryViewHolder(itemView: View) : GenericViewHolder<Repository>(itemView) {
    override fun bindData(data: Repository, position: Int, isItemSelected: Boolean, isEnabled: Boolean, isExpanded: Boolean) {
        textView_repo_title.text = data.name
    }
}
