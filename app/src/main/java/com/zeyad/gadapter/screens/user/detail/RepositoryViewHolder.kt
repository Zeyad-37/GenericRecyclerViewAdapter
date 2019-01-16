package com.zeyad.gadapter.screens.user.detail

import android.view.View
import com.zeyad.gadapter.GenericViewHolder

internal class RepositoryViewHolder(itemView: View) : GenericViewHolder<Repository>(itemView) {

    override fun bindData(repository: Repository, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {
        itemView.textView_repo_title.text = repository.name
    }
}
