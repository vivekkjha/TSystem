package com.vivek.tsystem.web;

import android.content.Context;


import com.vivek.tsystem.common.utils.InternetUtil;

import javax.inject.Inject;

/**
 * Created by vivek on 08/12/17.
 */

public class InternetStatusImpl implements IInternetStatus {

    private Context context;

    @Inject
    public InternetStatusImpl(Context context)
    {
        this.context = context;
    }

    @Override
    public boolean isConnected() {
        return InternetUtil.isNetworkAvailable(context);
    }
}
