package com.example.punit.twitterclient.dagger.modules;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.punit.twitterclient.TwitterApplication;
import com.example.punit.twitterclient.util.Constants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final TwitterApplication application;
    public AppModule(TwitterApplication application){
        this.application = application;
    }

    @Singleton
    @Provides
    Context providesContext(){
        return this.application;
    }

    @Singleton
    @Provides
    SharedPreferences provideSharedPreferences(Context context){
        return context.getSharedPreferences(Constants.PREF_NAME,0);
    }
}
