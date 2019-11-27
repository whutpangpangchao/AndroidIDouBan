package com.example.idouban.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecycleViewHolder<T> extends RecyclerView.ViewHolder {
    protected T itemContent;

    public BaseRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void updateItem(T itemContent) {
        this.itemContent = itemContent;
        onBindItem(itemContent);
    }

    protected abstract void onBindItem(T itemContent);

    public interface BuilderItemViewHolder<VH> {
        VH build(View itemView);
    }
}