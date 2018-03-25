package com.vivek.tsystem.common.di;

import android.content.Context;


import com.vivek.tsystem.BuildConfig;
import com.vivek.tsystem.R;
import com.vivek.tsystem.common.value.Constants;
import com.vivek.tsystem.web.IInternetStatus;
import com.vivek.tsystem.web.InternetStatusImpl;

import java.io.File;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;

/**
 * Created by vivek on 08/12/17.
 */
@Module
public class ValueModule {

    @Provides
    @Singleton
    @Named("isDebug")
    boolean provideIsDebug() {
        return BuildConfig.DEBUG;
    }

    @Provides
    @Singleton
    @Named("networkTimeoutInSeconds")
    int provideNetworkTimeoutInSeconds() {
        return Constants.Api.NETWORK_CONNECTION_TIMEOUT;
    }

    @Provides
    @Singleton
    HttpUrl provideEndpoint(Context context) {
        return HttpUrl.parse(context.getString(R.string.BASE_API_URL));
    }


    @Provides
    @Singleton
    @Named("cacheSize")
    long provideCacheSize() {
        return Constants.Api.CACHE_SIZE;
    }

    @Provides
    @Singleton
    @Named("cacheMaxAge")
    int provideCacheMaxAgeMinutes() {
        return Constants.Api.CACHE_MAX_AGE;
    }

    @Provides
    @Singleton
    @Named("cacheMaxStale")
    int provideCacheMaxStaleDays() {
        return Constants.Api.CACHE_MAX_STALE;
    }

    @Provides
    @Singleton
    @Named("retryCount")
    public int provideApiRetryCount() {
        return Constants.Api.API_RETRY_COUNT;
    }

    @Provides
    @Singleton
    @Named("cacheDir")
    File provideCacheDir() {
        return new File("build");
    }

    @Provides
    @Singleton
    Logger provideLogger() {
        return Logger.getAnonymousLogger();
    }

    @Provides
    @Singleton
    public IInternetStatus provideStateManager(InternetStatusImpl internetStatus) {
        return internetStatus;
    }

}
