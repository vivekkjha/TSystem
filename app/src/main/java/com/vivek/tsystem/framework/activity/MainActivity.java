package com.vivek.tsystem.framework.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vivek.tsystem.R;
import com.vivek.tsystem.common.app.TSystemApplication;
import com.vivek.tsystem.common.database.DataSourceHelper;
import com.vivek.tsystem.common.error.NoDataException;
import com.vivek.tsystem.common.error.NoMoreDataException;
import com.vivek.tsystem.common.utils.LogUtil;
import com.vivek.tsystem.common.utils.Utility;
import com.vivek.tsystem.common.value.Constants;
import com.vivek.tsystem.framework.datamodel.FlickrObj;
import com.vivek.tsystem.framework.model.MainActivityModel;
import com.vivek.tsystem.helper.recycler.AppRecycler;
import com.vivek.tsystem.helper.recycler.GridRecyclerWrapper;
import com.vivek.tsystem.mvparch.MvpView;
import com.vivek.tsystem.mvparch.PresenterImpl;

import java.util.List;

import javax.inject.Inject;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressWarnings("unchecked")
public class MainActivity extends BaseActivity implements MvpView<MainActivityModel,MainActivityModel.MainActivityQuery,MainActivityModel.MainActivityUA> ,EasyPermissions.PermissionCallbacks{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_FILE_STORAGE_APP_PERM = 124;

    private UserActionListener mUserActionListener;
    private PresenterImpl mPresenter;

    private String mSearchString;
    private int mGridColumns = 2;
    private int mPage = 1;
    private int mLimit = 10;
    private boolean mNoMoreData = false;

    private GridRecyclerWrapper<FlickrObj.Photo> mRecycler;
    private FrameLayout frame_content;
    private SwipeRefreshLayout mRefreshLayout;
    private SearchView searchView;
    private TextView txt_empty;

    @Inject
    DataSourceHelper dataSourceHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initializeViews(Bundle bundle) {
        TSystemApplication.getAppComponent().inject(this);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        frame_content =  findViewById(R.id.frame_content);
        setSupportActionBar(toolbar);
        txt_empty = findViewById(R.id.txt_empty);
        mRefreshLayout = findViewById(R.id.refresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        initPresenter();
    }

    /**
     * Initialize presenter
     */
    private void initPresenter(){
        MainActivityModel model = new MainActivityModel();
        mPresenter = new PresenterImpl(model,this,model.getUserActions(),model.getAllQueries());
        mPresenter.loadInitial();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(searchView!=null) {
            searchView.clearFocus();
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.cleanUp();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setVisible(true);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mUserActionListener.onUserAction(MainActivityModel.MainActivityUA.DISPOSE_TASKS,null);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.equals(mSearchString)){
                    //> If query is same as old one return
                    return false;
                }
                if(mRecycler!=null){
                    mRecycler.reset();
                }
                mSearchString = query;
                mPage = 1;
                requestPermissionsAndGetPhotos();
                Utility.hideSoftKeyboard(searchView);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText) && !TextUtils.isEmpty(mSearchString))
                {
                    if(mRecycler!=null){
                        mRecycler.reset();
                    }
                    mSearchString = null;
                    Utility.hideSoftKeyboard(searchView);
                    return true;
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.grid_size_2) {
            if(mRecycler!=null && mGridColumns!=2)
            {
                mGridColumns = 2;
                mRecycler.setGridColumns(mGridColumns);
            }
            return true;
        }
        if (id == R.id.grid_size_3) {
            if(mRecycler!=null && mGridColumns!=3)
            {
                mGridColumns = 3;
                mRecycler.setGridColumns(mGridColumns);
            }
            return true;
        }
        if (id == R.id.grid_size_4) {
            if(mRecycler!=null && mGridColumns!=4)
            {
                mGridColumns = 4;
                mRecycler.setGridColumns(mGridColumns);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayData(MainActivityModel model, MainActivityModel.MainActivityQuery query) {
        LogUtil.d(TAG,query.name());
        switch (query){
            case INITIALIZE:
                break;
        }
    }

    @Override
    public void displayErrorMessage(MainActivityModel.MainActivityQuery query, Throwable t) {
        LogUtil.d(TAG,query.name(),t);
        switch (query){
            case INITIALIZE:
                break;
        }

    }

    @Override
    public void displayUserActionResult(MainActivityModel model, MainActivityModel.MainActivityUA userAction) {
        LogUtil.d(TAG,userAction.name());
        switch (userAction){
            case GET_PHOTOS:
                if(mRefreshLayout.isRefreshing()){
                    mRefreshLayout.setRefreshing(false);
                    mRefreshLayout.setEnabled(false);
                }
                if(model.getPhotoList() == null){
                    displayUserActionError(model,userAction,new NoDataException());
                    return;
                }
                setUpGridRecycler(model.getPhotoList());
                break;
            case DISPOSE_TASKS:
                if(mRefreshLayout.isRefreshing()){
                    mRefreshLayout.setRefreshing(false);
                    mRefreshLayout.setEnabled(false);
                }
                break;
        }
    }

    @Override
    public void displayUserActionError(MainActivityModel model, MainActivityModel.MainActivityUA userAction, Throwable throwable) {
        LogUtil.d(TAG,userAction.name(),throwable);
        switch (userAction){
            case GET_PHOTOS:
                if(mRefreshLayout.isRefreshing()){
                    mRefreshLayout.setRefreshing(false);
                    mRefreshLayout.setEnabled(false);
                }
                if(throwable instanceof NoMoreDataException)
                {
                    mNoMoreData = true;
                    return;
                }
                if(throwable instanceof NoDataException)
                {
                    Utility.showToast(getContext(),getString(R.string.couldnt_fetch_data));
                    return;
                }
                break;
        }
    }

    @Override
    public Context getContext() {
        return MainActivity.this;
    }

    @Override
    public void addListener(UserActionListener listener) {
        mUserActionListener = listener;
    }


    /**
     * Set up grid recycler and refresh its values
     * @param list list to add or update
     */
    private void setUpGridRecycler(List<FlickrObj.Photo> list){

        if(mRecycler!=null){
            if(mRecycler.getItems().size()>0) {
                mRecycler.addItems(list);
            }
            else{
                mRecycler.setItems(list);
            }
            mRecycler.refreshView();
            return;
        }

        mRecycler = new GridRecyclerWrapper<FlickrObj.Photo>(getContext()) {
            @Override
            protected ViewHolder getGridItemViewHolder(int type) {
                View itemView = LayoutInflater.from(getContext())
                        .inflate(R.layout.layout_photo_item, null, false);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT));
                return new GridViewHolder(itemView);
            }

            @Override
            protected void bindGridViewHolder(ViewHolder viewHolder, int index, FlickrObj.Photo data) {
                if(viewHolder instanceof GridViewHolder){
                    ((GridViewHolder) viewHolder).setData(data);
                }

                //> load more pages
                if (index == getItemCount() - 1 && !mNoMoreData) {
                    ++mPage;
                    requestPermissionsAndGetPhotos();
                }
            }

            @Override
            public int gridColumns() {
                return mGridColumns;
            }
        };
        mRecycler.setIItemsObserverListener(new AppRecycler.IItemsObserverListener() {
            @Override
            public void onNoItem() {
                txt_empty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onGotItems(int total) {
                txt_empty.setVisibility(View.GONE);

            }
        });
        mRecycler.setItems(list);
        mRecycler.refreshView();
        frame_content.removeAllViews();
        frame_content.addView(mRecycler,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
    }

    @AfterPermissionGranted(RC_FILE_STORAGE_APP_PERM)
    private void requestPermissionsAndGetPhotos() {

        String[] perms = { Manifest.permission.WRITE_EXTERNAL_STORAGE };
        if (EasyPermissions.hasPermissions(this, perms)) {
            if(TextUtils.isEmpty(mSearchString)) {
                return;
            }
            mRefreshLayout.setEnabled(true);
            if(!mRefreshLayout.isRefreshing()){
                mRefreshLayout.setRefreshing(true);
            }
            Bundle bundle =  new Bundle();
            bundle.putInt(Constants.Bundle.PAGE,mPage);
            bundle.putInt(Constants.Bundle.LIMIT,mLimit);
            bundle.putString(Constants.Bundle.TEXT,mSearchString);
            mUserActionListener.onUserAction(MainActivityModel.MainActivityUA.GET_PHOTOS,bundle);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_app_perm), RC_FILE_STORAGE_APP_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtil.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtil.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
            return;
        }
        this.finish();
    }

    private class GridViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        GridViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }

        public void setData(FlickrObj.Photo data){
            if(data == null){
                return;
            }
            String url = data.getPhotoUrl();
            LogUtil.d(TAG,"Url for image : " + data.getPhotoUrl());

            //> Use local path if exists for same Url and Query string
            String localPath = dataSourceHelper.getLocalImagePath(mSearchString,data.getPhotoUrl());
            if(!TextUtils.isEmpty(localPath)) {
                LogUtil.d(TAG,"Local path : " + localPath);
                url = localPath;
            }
            Glide.with(getContext()).load(url).into(image);

            String finalUrl = url;
            image.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this,FullViewActivity.class);
                intent.putExtra(Constants.Bundle.URL, finalUrl);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, v, getString(R.string.image_transition));
                startActivity(intent, options.toBundle());
            });

        }
    }
}
