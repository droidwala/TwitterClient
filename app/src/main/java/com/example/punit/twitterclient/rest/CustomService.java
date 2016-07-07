package com.example.punit.twitterclient.rest;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.model.Timeline;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.MediaService;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

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

    @GET("/1.1/statuses/home_timeline.json")
    void showLatestTimeline(@Query("since_id") long id,
                            Callback<List<Timeline>> cb);

    @GET("/1.1/search/tweets.json")
    void searchTweets(@Query(encodeValue = true,value = "q") String screen_name,
                      @Query("since_id") long id,
                      Callback<Search> cb);
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

    //Compose tweet
    @POST("/1.1/statuses/update.json")
    void postTweet(@Query(encodeValue = true,value = "status") String tweet,
                   @Query("in_reply_to_status_id") long id,
                   Callback<Response> cb);


    //Compose tweet
    @POST("/1.1/statuses/update.json")
    void postTweetWithVideo(@Query(encodeValue = true,value = "status") String tweet,
                            @Query("in_reply_to_status_id") long id,
                            @Query("media_ids") String media_id,
                            Callback<Response> cb);

    @Multipart
    @POST("/1.1/media/upload.json")
    void uploadINIT(@Part("command") String command,
                    @Part("media_type") String media_type,
                    @Part("total_bytes") long bytes,
                    Callback<Media> cb);

    @Multipart
    @POST("/1.1/media/upload.json")
    void uploadAPPEND(@Part("command") String command,
                      @Part("media_id") String mediaid,
                      @Part("media")TypedFile media,
                      @Part("segment_index") int index,
                      Callback<Response> cb);


    @Multipart
    @POST("/1.1/media/upload.json")
    void uploadFINALIZE(@Part("command") String command,
                        @Part("media_id") String mediaid,
                        Callback<Media> cb);



}

