package com.vivek.tsystem.common.di;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by vivek on 08/12/17.
 */

@Module
public class AppModule {

    private Application application;

    public AppModule(Application application)
    {
        this.application = application;
    }

    @Singleton
    @Provides
    public Context appContext()
    {
        return application;
    }

    @Singleton
    @Provides
    public Gson getGson()
    {
        return new GsonBuilder().create();
    }

    @Singleton
    @Provides
    public Resources getResources()
    {
        return appContext().getResources();
    }
}
