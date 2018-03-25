package com.vivek.tsystem.helper.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper of recycler view with frequently used methods wrapped in one class
 */
public abstract class AppRecycler<T> extends RecyclerView {

    private DisplayMode mCurrentDisplayMode = DisplayMode.LIST;
    private Adapter mRecyclerAdapter;
    private List<T> mItems = new ArrayList<T>();
    private IItemsObserverListener mIItemsObserverListener;
    private IRecyclerItemClicked<T> mIRecyclerItemClicked;
    private IRecyclerLongPress<T> mIRecyclerLongPress;
    private int gridColumns = 3;

    public AppRecycler(Context context) {
        super(context);
        this.init();
    }
    public AppRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AppRecycler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    private void init()
    {
        this.mRecyclerAdapter = new Adapter()
        {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (isShowList())
                    return getListItemViewHolder(viewType);
                else
                    return getGridItemViewHolder(viewType);
            }

            @Override
            public int getItemViewType(int position) {
                return AppRecycler.this.getItemViewType(position);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, final int position) {
                final T data = position >= mItems.size() ? null : mItems.get(position);
                if (isShowList()) {
                    bindListViewHolder(holder, position, data);
                } else {
                    bindGridViewHolder(holder, position, data);
                }

                if(mIRecyclerItemClicked!=null) {
                    if(getChildClickableViews()!=null) {
                        for(View clickableView : getChildClickableViews())
                        {
                            clickableView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mIRecyclerItemClicked.onItemClicked(position, data, v);
                                }
                            });
                        }
                    }
                    else
                    {
                        holder.itemView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mIRecyclerItemClicked.onItemClicked(position,data,v);
                            }
                        });
                    }
                }
                if(mIRecyclerLongPress!=null)
                {
                    if(isLongClickListenerOnChildViews())
                    {
                        //> add item click listener to each view in group
                        if (holder.itemView instanceof ViewGroup) {
                            ViewGroup group = (ViewGroup) holder.itemView;
                            for (int i = 0; i < group.getChildCount(); i++) {
                                View view = group.getChildAt(i);
                                view.setOnLongClickListener(new OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        return mIRecyclerLongPress.onLongPress(position, data, v);
                                    }
                                });
                            }
                        }
                    }
                    else
                    {
                        holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return mIRecyclerLongPress.onLongPress(position, data, v);
                            }
                        });
                    }
                }
            }

            @Override
            public int getItemCount() {
                return AppRecycler.this.getItemCount();
            }
        };

        if (showListInitially()) {
            this.setAdapter(DisplayMode.LIST);
        } else if (getDisplayMode() == DisplayMode.GRID) {
            this.setAdapter(DisplayMode.GRID);
        }
    }

    /**
     * Set adapter of recycler view
     * @param mode Display mode of recycler
     */
    public void setAdapter(DisplayMode mode)
    {
        this.mCurrentDisplayMode = mode;
        if(mode == DisplayMode.LIST)
        {
            final LinearLayoutManager layoutManager = getListViewTypeLayoutManager();
            if(getListOrientation() == ListOrientation.VERTICAL)
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            else
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            this.setLayoutManager(layoutManager);
            if(getItemDecorator()!=null)
                this.addItemDecoration(getItemDecorator());
        }
        else {
            this.setLayoutManager(getGridViewTypeLayoutManager());
        }
        this.setAdapter(this.mRecyclerAdapter);
    }

    @NonNull
    protected LinearLayoutManager getLinearLayoutManager() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setAutoMeasureEnabled(true);
        return layoutManager;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (!isShowList()) {
            int columnWidth = getMinColumnWidth();
            if (columnWidth > 0) {
                int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
                ((GridLayoutManager)getLayoutManager()).setSpanCount(spanCount);
            }
        }
    }
    protected int getMinColumnWidth() {
        return 0;
    }

    @NonNull
    protected GridLayoutManager getGridLayoutManager() {
        return new GridLayoutManager(this.getContext(), gridColumns());
    }

    /**
     * Get item count
     * @return item count of recycler
     */
    public int getItemCount() {
        return AppRecycler.this.getItems().size();
    }

    /**
     * Get list layout manager, override this method to set you own layout manager to list
     * @return layout manager for list view type
     */
    protected LinearLayoutManager getListViewTypeLayoutManager()
    {
        return getLinearLayoutManager();
    }

    /**
     * Gt list layout manager, override this method to set you own layout manager to grid
     * @return layout manager for grif view type
     */
    protected GridLayoutManager getGridViewTypeLayoutManager()
    {
        return getGridLayoutManager();
    }

    /**
     * Checl whether the mode of display is List
     * @return true, if list false otherwise
     */
    public boolean isShowList() {
        return getDisplayMode() == DisplayMode.LIST;
    }

    /**
     * Override this method to change orientation of list
     * @return default vertical
     */
    protected ListOrientation getListOrientation()
    {
        return ListOrientation.VERTICAL;
    }
    /**
     * Override this method to show item decoration at the bottom of list item
     * @return false by default, else overridden value
     */
    protected ItemDecoration getItemDecorator()
    {
        return null;
    }

    protected List<View> getChildClickableViews()
    {
        return null;
    }

    /**
     * Check if each child of the item view get a long click listener
     * @return default false, override to change value
     */
    protected boolean isLongClickListenerOnChildViews()
    {
        return false;
    }
    /**
     * to change view mode when adapter initializes
     * @return true, if list ; false for grid
     */
    protected boolean showListInitially() {return  true;}
    protected abstract DisplayMode getDisplayMode(); //> get Display mode
    protected abstract ViewHolder getListItemViewHolder(int type); //> get item viewholder for list
    protected abstract ViewHolder getGridItemViewHolder(int type); //> get item viewholder for grid
    protected abstract void bindGridViewHolder(ViewHolder viewHolder, int index, T data); //> bind grid viewholder
    protected abstract void bindListViewHolder(ViewHolder viewHolder, int index, T data); //> bind list viewholder
    /**
     * override this method to change numer of columns in grid
     * @return no of columns in grid
     */
    protected int gridColumns() {
        return  gridColumns;
    }

    public void setGridColumns(int gridColumns) {
        this.gridColumns = gridColumns;
        init();
        refreshView();
    }

    /**
     * get item View type to load in viewholder
     * @param position position
     * @return view type
     */
    protected int getItemViewType(int position) {return  0;};

    /**
     * Append items on pagination, override this method to reset adapter
     * with single list rather than just adding item
     * @return false, if need to reset ; false by default
     */
    protected boolean appendItemsOnPagination(){
        return true;
    }

    /**
     * get items populated in list
     * @return list of adapter items
     */
    public List<T> getItems() {
        return mItems;
    }

    /**
     * Set list items
     * @param mItems items
     */
    public void setItems(List<T> mItems) {
        if (mItems != null) {
            this.mItems = new ArrayList<T>(mItems);
        } else {
            this.mItems = new ArrayList<T>();
        }
    }

    /**
     * Notify observer about the data count
     */
    private void notifyItems() {
        int count = mRecyclerAdapter.getItemCount();
        if (mIItemsObserverListener != null) {
            if (count == 0) {
                mIItemsObserverListener.onNoItem();
            } else {
                mIItemsObserverListener.onGotItems(count);
            }
        }
    }


    /**
     * Reset recycler view and its adapter to square one
     */
    public void reset(){
        if (this.mItems != null)
            mItems.clear();
        this.refreshView();
    }

    public void notifyDataChangedAtPosition(int pos) {
        this.mRecyclerAdapter.notifyItemChanged(pos);
        notifyItems();
    }
    public void notifyItemRangeInserted(int positionStart, int itemCount) {
        this.mRecyclerAdapter.notifyItemRangeInserted(positionStart, itemCount);
        notifyItems();
    }
    public void refreshView() {
        if (this.mRecyclerAdapter == null) return;
        this.mRecyclerAdapter.notifyDataSetChanged();
        notifyItems();
    }

    public void addItems(List<T> items) {
        this.mItems.addAll(items);
        this.mRecyclerAdapter.notifyDataSetChanged();
        notifyItems();
    }

    public void addItem(T item) {
        this.addItem(mItems.size(), item);
    }
    public  void addItem(int index, T item) {
        this.mItems.add(index, item);
        this.mRecyclerAdapter.notifyItemInserted(index);
        notifyItems();
    }

    public  void removeItem(int index) {
        this.mItems.remove(index);
        this.mRecyclerAdapter.notifyItemRemoved(index);
        notifyItems();
    }

    public  void removeItem(T data) {
        this.mItems.remove(data);
        this.mRecyclerAdapter.notifyDataSetChanged();
        notifyItems();
    }

    public T getItemAt(int pos) {
        if (pos < 0 || pos >= mItems.size()) return null;
        return mItems.get(pos);
    }

    protected int getLoadNextPosition() {
        return mRecyclerAdapter.getItemCount() - 1;
    }

    public void setIItemsObserverListener(IItemsObserverListener mIItemsObserverListener) {
        this.mIItemsObserverListener = mIItemsObserverListener;
    }

    public void setIRecyclerItemClicked(IRecyclerItemClicked<T> mIRecyclerItemClicked) {
        this.mIRecyclerItemClicked = mIRecyclerItemClicked;
    }

    public void setIRecyclerLongPress(IRecyclerLongPress<T> mIRecyclerLongPress) {
        this.mIRecyclerLongPress = mIRecyclerLongPress;
    }

    /**
     * Enumeration for display mode
     */
    public enum DisplayMode{ LIST,GRID}
    public enum ListOrientation{ VERTICAL,HORIZONTAL}

    public enum RefreshType {INSERTED, RANGE_CHANGED, MODIFIED, DELETED, DATA_SET_CHANGED}

    /**
     * Data items observer interface
     */
    public interface IItemsObserverListener {
        void onNoItem();
        void onGotItems(int total);
    }

    public interface IRecyclerItemClicked<T> {
        void onItemClicked(int index, T data, View view);
    }

    public interface IRecyclerLongPress<T> {
        boolean onLongPress(int index, T data, View view);
    }

}
