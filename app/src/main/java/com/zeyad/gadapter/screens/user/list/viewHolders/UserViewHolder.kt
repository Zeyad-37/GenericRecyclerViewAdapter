package com.zeyad.gadapter.screens.user.list.viewHolders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zeyad.gadapter.GenericViewHolder
import com.zeyad.gadapter.screens.user.User

class UserViewHolder(itemView: View) : GenericViewHolder<User>(itemView) {

    override fun bindData(userModel: User, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {
        if (userModel.avatarUrl.isNotEmpty()) {
            Glide.with(itemView.context).load(userModel.avatarUrl).into(itemView.avatar)
        } else {
            Glide.with(itemView.context)
                    .load(if ((Math.random() * 10).toInt() % 2 == 0)
                        "https://github.com/identicons/jasonlong.png"
                    else
                        "https://help.github.com/assets/images/help/profile/identicon.png")
                    .into(itemView.avatar)
        }
        itemView.title.text = userModel.login
        itemView.setBackgroundColor(if (isItemSelected) Color.GRAY else Color.WHITE)
    }

    fun getAvatar(): ImageView = itemView.avatar
    fun getTextViewTitle(): TextView = itemView.title
}
