package com.example.punit.twitterclient.rest;

import android.content.Context;

import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.Kit;

/**
 * TwitterApiClient is HttpClient built into twitter sdk which handles all the oauth 1.0(Auth) process internally.
 * By extending the TwitterApiClient all the authentication process need to make API request is handled by it.
 */
public class MyTwitterApiClient extends TwitterApiClient {


    /**
     * Must be instantiated after {@link TwitterCore} has been
     * initialized via {@link Fabric#with(Context, Kit[])}.
     *
     * @param session Session to be used to create the API calls.
     * @throws IllegalArgumentException if TwitterSession argument is null
     */
    public MyTwitterApiClient(Session session) {
        super(session);
    }

    public CustomService getCustomService(){
        return getService(CustomService.class);
    }

}
