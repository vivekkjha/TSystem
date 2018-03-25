package com.vivek.tsystem.common.app;

import android.app.Application;
import android.content.Context;

import com.vivek.tsystem.common.di.AppComponent;
import com.vivek.tsystem.common.di.AppModule;
import com.vivek.tsystem.common.di.DaggerAppComponent;
/**
 * Created by vivek on 24/03/18.
 */

public class TSystemApplication extends Application {

    private final String TAG = TSystemApplication.class.getSimpleName();
    private static TSystemApplication mInstance;
    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mAppComponent = createAppComponent();

    }
    public AppComponent createAppComponent()
    {

        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static TSystemApplication getInstance() {
        return mInstance;
    }
    public static Context getContext() {
        return mInstance.getApplicationContext();
    }


}
