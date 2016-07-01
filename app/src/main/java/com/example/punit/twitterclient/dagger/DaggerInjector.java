package com.example.punit.twitterclient.dagger;

import com.example.punit.twitterclient.TwitterApplication;
import com.example.punit.twitterclient.dagger.components.AppComponent;
import com.example.punit.twitterclient.dagger.components.DaggerAppComponent;
import com.example.punit.twitterclient.dagger.modules.AppModule;

public class DaggerInjector {


    private static AppComponent appComponent;
    public static void init(TwitterApplication application){
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(application)).build();
    }

    public static AppComponent getAppComponent(){
        return appComponent;
    }



}
