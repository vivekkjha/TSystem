package com.vivek.tsystem.framework.model;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.vivek.tsystem.common.app.TSystemApplication;
import com.vivek.tsystem.common.database.DataSourceHelper;
import com.vivek.tsystem.common.error.ArgsRequiredException;
import com.vivek.tsystem.common.error.FileAlreadyExistException;
import com.vivek.tsystem.common.error.NoDataException;
import com.vivek.tsystem.common.error.NoMoreDataException;
import com.vivek.tsystem.common.utils.AppFileUtils;
import com.vivek.tsystem.common.value.Constants;
import com.vivek.tsystem.framework.datamodel.FlickrObj;
import com.vivek.tsystem.mvparch.IMvpQuery;
import com.vivek.tsystem.mvparch.IUserAction;
import com.vivek.tsystem.mvparch.Model;
import com.vivek.tsystem.web.InternetStatusImpl;
import com.vivek.tsystem.web.retrofit.FlickrApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivityModel implements Model<MainActivityModel.MainActivityQuery,MainActivityModel.MainActivityUA> {

    private static final String TAG = MainActivityModel.class.getSimpleName();
    private static final String EXTENSION = "jpg";

    private CompositeDisposable compositeDisposable;
    private List<FlickrObj.Photo> mPhotoList;

    @Inject
    Retrofit mClient;

    @Inject
    Gson gson;

    @Inject
    InternetStatusImpl mInternetStatus;

    @Inject
    Context context;

    @Inject
    AppFileUtils mAppFileUtils;

    @Inject
    DataSourceHelper dataSourceHelper;

    @Override
    public MainActivityQuery[] getAllQueries() {
        return  MainActivityQuery.values();
    }

    @Override
    public MainActivityUA[] getUserActions() {
        return MainActivityUA.values();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deliverUserAction(final MainActivityUA action, @Nullable Bundle args, final UserActionCallback userActionCallback) {
        switch (action)
        {
            case GET_PHOTOS:
                //> return error if there is no argument (can be a serialize object or key value pairs)
                if(args == null)
                {
                    userActionCallback.onError(action, new ArgsRequiredException());
                    return;
                }
                String text =  args.getString(Constants.Bundle.TEXT);
                int page =  args.getInt(Constants.Bundle.PAGE);
                int limit =  args.getInt(Constants.Bundle.LIMIT);
                if(page == 0 || limit ==0)
                {
                    userActionCallback.onError(action, new ArgsRequiredException());
                    return;
                }

                /*
                 * Photos Observer either from Local DB or From network
                 */
                SingleObserver<List<FlickrObj.Photo>> photosRxObserver =  new SingleObserver<List<FlickrObj.Photo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(List<FlickrObj.Photo> photos) {
                        mPhotoList = photos;
                        userActionCallback.onModelUpdated(MainActivityModel.this,action);
                    }

                    @Override
                    public void onError(Throwable e) {
                        userActionCallback.onError(action,e);
                    }
                };

                //> From local store
                if(!mInternetStatus.isConnected())
                {
                    //> Return all data in single page in case of offline
                    if(page>1)
                    {
                        userActionCallback.onError(action,new NoMoreDataException());
                        return;
                    }
                    getLocallySavedDataObservable(text)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(photosRxObserver);
                    return;
                }

                //> From network
                FlickrApi flickrApi = mClient.create(FlickrApi.class);
                flickrApi.getPhotos(Constants.AppConstants.FLICKR_SEARCH_METHOD,Constants.AppConstants.FLICKR_API_KEY,text,
                        Constants.AppConstants.OUTPUT_FORMAT,Constants.AppConstants.NO_JSON_CALLBACK,limit,page)
                        .flatMap(flickrObj -> {

                            if(flickrObj== null){
                                return Single.error(new NoDataException());
                            }

                            FlickrObj.PhotosWrapper photosWrapper = flickrObj.getPhotosWrapper();
                            if(photosWrapper == null){
                                return Single.error(new NoDataException());
                            }
                            //> If last page reached
                            if(photosWrapper.getPage() == photosWrapper.getPages())
                            {
                                return Single.error(new NoMoreDataException());
                            }
                            List<FlickrObj.Photo> photos = photosWrapper.getPhoto();
                            if(photos!=null){
                                for(FlickrObj.Photo photo : photos){
                                    //> Make url for Images and store it in same POJO
                                    String url = String.format(Locale.getDefault(),"https://farm%d.staticflickr.com/%s/%s_%s_q.jpg",
                                            photo.getFarm(),photo.getServer(),photo.getId(),photo.getSecret());
                                    photo.setPhotoUrl(url);
                                }
                                return Single.just(photos);
                            }
                            return Single.error(new NoDataException());
                        })
                        .map(photos -> {
                            getFileInDirectoryAndStoreData(text,photos);
                            return photos;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(photosRxObserver);
                break;

        }
    }

    public List<FlickrObj.Photo> getPhotoList() {
        return mPhotoList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void requestData(MainActivityQuery query, DataQueryCallback callback) {
        if(query == MainActivityQuery.INITIALIZE)
        {
            TSystemApplication.getAppComponent().inject(this);
            compositeDisposable = new CompositeDisposable();
            callback.onModelUpdated(this,query);
        }
    }

    @Override
    public void cleanUp() {
        if(compositeDisposable!=null && !compositeDisposable.isDisposed()){
            compositeDisposable.dispose();
        }
    }

    public enum MainActivityQuery implements IMvpQuery
    {
        INITIALIZE(0);

        int id;
        MainActivityQuery(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    public enum MainActivityUA implements IUserAction
    {
        GET_PHOTOS(1);

        int id;
        MainActivityUA(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }

    /**
     * Get Single observable from the method which loads data from database async on condition of search query
     * @param searchString query string
     * @return Single Observable to return List of Photo object
     */
    private Single<List<FlickrObj.Photo>> getLocallySavedDataObservable(String searchString){
        return Single.fromCallable(() -> {
            List<FlickrObj.Photo> list = new ArrayList<>();
            List<String> pathList = dataSourceHelper.getAllImagePaths(searchString);
            if(pathList.size()!=0) {
                for (String localPath : pathList) {
                    list.add(new FlickrObj.Photo(localPath));
                }
            }

            return list;
        });
    }

    /**
     * Get file from URL and store data in database
     * @param queryName name of search query
     * @param flickerPhotos photo object to fetch url from
     */
    private void getFileInDirectoryAndStoreData(String queryName, List<FlickrObj.Photo> flickerPhotos) {
        if (flickerPhotos == null || flickerPhotos.size() == 0) {
            return;
        }
            Observable.fromIterable(flickerPhotos)
                    .flatMap(photo -> {
                        //> Null checks
                        if (photo == null || photo.getPhotoUrl().isEmpty()) {
                            return Observable.error(new NoDataException());
                        }
                        //> Check if file has already been downloaded previously and exists in directory
                        String localPath = dataSourceHelper.getLocalImagePath(queryName, photo.getPhotoUrl());
                        if (!TextUtils.isEmpty(localPath)) {
                            File file = new File(localPath);
                            if (file.exists()) {
                                return Observable.error(new FileAlreadyExistException());
                            }
                        }
                        //> If doesn't exists, then proceed
                        return Observable.just(photo);
                    })
                    .filter(photo -> !TextUtils.isEmpty(photo.getPhotoUrl()))
                    .map(photo -> {
                        //> get file from network and store it in destination path
                        File destinationFile = mAppFileUtils.createFileAt(context, photo.getTitle(), EXTENSION);
                        File storedFile = mAppFileUtils.getFile(photo.getPhotoUrl(), null, destinationFile);
                        return new Pair<>(photo.getPhotoUrl(), storedFile == null ? "" : storedFile.getAbsolutePath());
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map(stringStringPair -> {
                        //> Store path in local database
                        dataSourceHelper.insertData(queryName, stringStringPair.first, stringStringPair.second);
                        return true;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {

                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
    }






}
