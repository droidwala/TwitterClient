package com.example.punit.twitterclient.rest;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Define various REST API Calls
 */
public interface CustomService {

    //Fetches Home timeline tweets for the logged in user
    @GET("/1.1/statuses/home_timeline.json")
    void showTimeline(@Query("count") int count,
                      Callback<List<Tweet>> cb);


    //Fetches tweets from last tweet(max_id-1) fetched
    @GET("/1.1/statuses/home_timeline.json")
    void showMoreTimeline(@Query("count") int count,
                          @Query("max_id") long id,
                          Callback<List<Tweet>> cb);

    //Favorites a tweet
    @POST("/1.1/favorites/create.json")
    void favoriteTweet(@Query("id") long id,
                       Callback<Response> cb);

    //UnFav a tweet
    @POST("/1.1/favorites/destroy.json")
    void unfavoriteTweet(@Query("id") long id,
                         Callback<Response> cb);
}
