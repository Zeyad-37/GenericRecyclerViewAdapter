package com.zeyad.app.screens.list.viewHolders

import android.graphics.Color
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zeyad.app.screens.User
import com.zeyad.gadapter.GenericViewHolder
import com.zeyad.gadapter.OnExpandListener
import kotlinx.android.synthetic.main.user_item_layout.view.*

class UserViewHolder(itemView: View) : GenericViewHolder<User>(itemView), OnExpandListener {
    override fun expand(isExpanded: Boolean) {
        itemView.tv_expand.visibility =
                if (isExpanded) {
                    VISIBLE
                } else {
                    GONE
                }
    }

    override fun bindData(data: User, position: Int, isItemSelected: Boolean, isEnabled: Boolean) {
        if (data.avatarUrl.isNotEmpty()) {
            Glide.with(itemView.context).load(data.avatarUrl).into(itemView.avatar)
        } else {
            Glide.with(itemView.context)
                    .load(if ((Math.random() * 10).toInt() % 2 == 0)
                        "https://github.com/identicons/jasonlong.png"
                    else
                        "https://help.github.com/assets/images/help/profile/identicon.png")
                    .into(itemView.avatar)
        }
        itemView.title.text = data.login
        itemView.setBackgroundColor(if (isItemSelected) Color.GRAY else Color.WHITE)
    }


    fun getAvatar(): ImageView = itemView.avatar
    fun getTextViewTitle(): TextView = itemView.title
}
