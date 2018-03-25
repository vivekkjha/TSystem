package com.vivek.tsystem.helper.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by vivekjha on 14/08/16.
 */
public abstract class GridRecyclerWrapper<T> extends AppRecycler<T>  {


    public GridRecyclerWrapper(Context context) {
        super(context);
    }

    public GridRecyclerWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridRecyclerWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected DisplayMode getDisplayMode() {
        return DisplayMode.GRID;
    }

    @Override
    protected boolean showListInitially() {
        return false;
    }

    @Override
    protected RecyclerView.ViewHolder getListItemViewHolder(int type) {
        return null;
    }

    @Override
    protected void bindListViewHolder(RecyclerView.ViewHolder viewHolder, int index, T data) {

    }


}
