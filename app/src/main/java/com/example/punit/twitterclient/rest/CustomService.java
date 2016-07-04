package com.example.punit.twitterclient.rest;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.model.Timeline;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Define various REST API Calls
 */
public interface CustomService {

    //Fetches Home timeline tweets for the logged in user
    @GET("/1.1/statuses/home_timeline.json")
    void showTimeline(@Query("count") int count,
                      Callback<List<Timeline>> cb);


    //Fetches tweets from last tweet(max_id-1) fetched
    @GET("/1.1/statuses/home_timeline.json")
    void showMoreTimeline(@Query("count") int count,
                          @Query("max_id") long id,
                          Callback<List<Timeline>> cb);

    //Favorites a tweet
    @POST("/1.1/favorites/create.json")
    void favoriteTweet(@Query("id") long id,
                       Callback<Response> cb);

    //UnFav a tweet
    @POST("/1.1/favorites/destroy.json")
    void unfavoriteTweet(@Query("id") long id,
                         Callback<Response> cb);

    //Retweet a tweet
    @POST("/1.1/statuses/retweet/{id}.json")
    void retweetTweet(@Path("id") long id,
                      Callback<Response> cb);

    //Reply to a tweet
    @POST("/1.1/statuses/update.json")
    void replyToTweet(@Query(encodeValue = true,value="status") String reply,
                      @Query("in_reply_to_status_id") long id,
                      Callback<Response> cb);

    //Compose tweet
    @POST("/1.1/statuses/update.json")
    void postTweet(@Query(encodeValue = true,value = "status") String tweet,
                   Callback<Response> cb);
}
