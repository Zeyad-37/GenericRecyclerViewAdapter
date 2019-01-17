package com.zeyad.gadapter.screens.user.detail

import android.view.View
import com.zeyad.gadapter.GenericViewHolder
import kotlinx.android.synthetic.main.repo_item_layout.view.*

internal class RepositoryViewHolder(itemView: View) : GenericViewHolder<Repository>(itemView) {

    override fun bindData(data: Repository, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {
        itemView.textView_repo_title.text = data.name
    }
}
