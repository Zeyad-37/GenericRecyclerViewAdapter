package com.zeyad.gadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class GenericViewHolder extends RecyclerView.ViewHolder {

    public GenericViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindData(@NonNull Object data, boolean itemSelected, int position, boolean isEnabled);
}
