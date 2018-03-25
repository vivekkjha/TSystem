package com.vivek.tsystem.common.di;

import com.vivek.tsystem.framework.activity.MainActivity;
import com.vivek.tsystem.framework.model.MainActivityModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by vivek on 08/12/17.
 */

@Singleton
@Component(modules = {AppModule.class, ApiModule.class, ValueModule.class})
public interface AppComponent {
    void inject(MainActivityModel target);
    void inject(MainActivity target);

}
