package com.example.punit.twitterclient;

import android.app.Application;
import android.util.Log;

import com.example.punit.twitterclient.dagger.DaggerInjector;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class TwitterApplication extends Application {

    private static final String TAG = "TwitterApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.KEY, BuildConfig.SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Log.d(TAG, "onCreate: " + BuildConfig.KEY + " " + BuildConfig.SECRET);
        DaggerInjector.init(this);
    }
}
