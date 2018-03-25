package com.vivek.tsystem.common.di;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.vivek.tsystem.web.IInternetStatus;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vivek on 08/12/17.
 */

@Module
public class ApiModule {

    private static final String HTTP_CACHE_PATH = "http-cache";
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String PRAGMA = "Pragma";

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClientWithAuthorization(@Named("loggingInterceptor") Interceptor loggingInterceptor,
                                                             @Named("networkTimeoutInSeconds") int networkTimeoutInSeconds,
                                                             @Named("isDebug") boolean isDebug,
                                                             Cache cache,
                                                             @Named("cacheInterceptor") Interceptor cacheInterceptor,
                                                             @Named("authInterceptor") Interceptor authInterceptor,
                                                             @Named("offlineInterceptor") Interceptor offlineCacheInterceptor,
                                                             @Named("retryInterceptor") Interceptor retryInterceptor) {

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(authInterceptor)
                .addInterceptor(offlineCacheInterceptor)
                .addInterceptor(retryInterceptor)
                .cache(cache)
                .connectTimeout(networkTimeoutInSeconds, TimeUnit.SECONDS);

        //show logs if app is in Debug mode
        if (isDebug) {
            okHttpClient.addInterceptor(loggingInterceptor);
        }

        return okHttpClient.build();
    }


    @Provides
    @Singleton
    public Cache provideCache(@Named("cacheDir") File cacheDir, @Named("cacheSize") long cacheSize) {
        Cache cache = null;

        try {
            cache = new Cache(new File(cacheDir.getPath(), HTTP_CACHE_PATH), cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cache;
    }

    @Singleton
    @Provides
    @Named("loggingInterceptor")
    public Interceptor loggingInterceptor(Logger logger) {
        return chain -> {
            Request request = chain.request();

            long t1 = System.nanoTime();
            logger.info(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            logger.info(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        };
    }

    @Singleton
    @Provides
    @Named("cacheInterceptor")
    public Interceptor provideCacheInterceptor(@Named("cacheMaxAge") int maxAgeMin) {
        return chain -> {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                    .maxAge(maxAgeMin, TimeUnit.MINUTES)
                    .build();

            return response.newBuilder()
                    .removeHeader(PRAGMA)
                    .removeHeader(CACHE_CONTROL)
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }

    @Singleton
    @Provides
    @Named("offlineInterceptor")
    public Interceptor provideOfflineCacheInterceptor(IInternetStatus internetStatus, @Named("cacheMaxStale") int maxStaleDay) {
        return chain -> {
            Request request = chain.request();

            if (!internetStatus.isConnected()) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(maxStaleDay, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }

    @Singleton
    @Provides
    @Named("authInterceptor")
    public Interceptor provideAuthorizationHeader() {
        return chain -> {
            String authHeader =  "";
            Request original = chain.request();
            if(original.header("No-Auth") == null) {
                if(authHeader.length() == 0){
                    throw new RuntimeException("Auth required for execution of this API");
                }
                else {
                    original = original.newBuilder()
                            .header("Authorization", authHeader)
                            .method(original.method(), original.body())
                            .build();
                }
            }
            return chain.proceed(original);
        };
    }


    @Singleton
    @Provides
    @Named("retryInterceptor")
    public Interceptor provideRetryInterceptor(@Named("retryCount") int retryCount) {
        return chain -> {
            Request request = chain.request();
            Response response = null;
            IOException exception = null;

            if(request.header("No-Retry") != null){
                return chain.proceed(request);
            }
            int tryCount = 0;
            while (tryCount < retryCount && (null == response || !response.isSuccessful())) {
                // retry the request
                try {
                    response = chain.proceed(request);
                } catch (IOException e) {
                    exception = e;
                } finally {
                    tryCount++;
                }
            }

            // throw last exception
            if (null == response && null != exception)
                throw exception;

            // otherwise just pass the original response on
            return response;
        };
    }

    @Provides
    @Singleton
    public Converter.Factory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    @Singleton
    public CallAdapter.Factory provideRxJavaCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }

    @Provides
    @Singleton
    public Retrofit provideAuthenticatedRetrofit(HttpUrl baseUrl, Converter.Factory converterFactory, CallAdapter.Factory callAdapterFactory, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(callAdapterFactory)
                .client(okHttpClient)
                .build();
    }


}
