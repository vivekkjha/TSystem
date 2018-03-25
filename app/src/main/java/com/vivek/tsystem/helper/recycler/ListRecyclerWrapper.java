package com.vivek.tsystem.helper.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by vivekjha on 14/08/16.
 */
public abstract class ListRecyclerWrapper<T> extends AppRecycler<T>  {


    public ListRecyclerWrapper(Context context) {
        super(context);
    }

    public ListRecyclerWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListRecyclerWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected DisplayMode getDisplayMode() {
        return DisplayMode.LIST;
    }

    @Override
    protected RecyclerView.ViewHolder getGridItemViewHolder(int type) {
        return null;
    }

    @Override
    protected void bindGridViewHolder(RecyclerView.ViewHolder viewHolder, int index, T data) {

    }

}
