package com.example.punit.twitterclient.model;

import com.google.gson.annotations.SerializedName;
import com.twitter.sdk.android.core.models.Coordinates;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.User;

import java.io.Serializable;

public class Timeline implements Serializable{

    public static final long INVALID_ID = -1L;


    /**
     * Nullable. Represents the geographic location of this Tweet as reported by the user or client
     * application. The inner coordinates array is formatted as geoJSON (longitude first,
     * then latitude).
     */
    @SerializedName("coordinates")
    public  Coordinates coordinates;

    /**
     * UTC time when this Tweet was created.
     */
    @SerializedName("created_at")
    public  String createdAt;

    /**
     * Perspectival. Only surfaces on methods supporting the include_my_retweet parameter, when set
     * to true. Details the Tweet ID of the user's own retweet (if existent) of this Tweet.
     */
    @SerializedName("current_user_retweet")
    public  Object currentUserRetweet;

    /**
     * Entities which have been parsed out of the text of the Tweet.
     */
    @SerializedName("entities")
    public TweetEntities entities;

    /**
     * Additional entities such as multi photos, animated gifs and video.
     */
    @SerializedName("extended_entities")
    public  TweetEntities extendedEtities;

    /**
     * Nullable. Indicates approximately how many times this Tweet has been "favorited" by Twitter
     * users.
     */
    @SerializedName("favorite_count")
    public  Integer favoriteCount;

    public void incrementFavCount(){
        this.favoriteCount +=1;
    }

    public void decrementFavCount(){
        this.favoriteCount -=1;
    }

    /**
     * Nullable. Perspectival. Indicates whether this Tweet has been favorited by the authenticating
     * user.
     */
    @SerializedName("favorited")
    public  boolean favorited;

    public void setfavorite(boolean isfav){
        this.favorited = isfav;
    }

    /**
     * Indicates the maximum value of the filter_level parameter which may be used and still stream
     * this Tweet. So a value of medium will be streamed on none, low, and medium streams.
     */
    @SerializedName("filter_level")
    public  String filterLevel;

    /**
     * The integer representation of the unique identifier for this Tweet. This number is greater
     * than 53 bits and some programming languages may have difficulty/silent defects in
     * interpreting it. Using a signed 64 bit integer for storing this identifier is safe. Use
     * id_str for fetching the identifier to stay on the safe side. See Twitter IDs, JSON and
     * Snowflake.
     */
    @SerializedName("id")
    public  long id;

    /**
     * The string representation of the unique identifier for this Tweet. Implementations should use
     * this rather than the large integer in id
     */
    @SerializedName("id_str")
    public  String idStr;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the screen name of
     * the original Tweet's author.
     */
    @SerializedName("in_reply_to_screen_name")
    public  String inReplyToScreenName;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the integer
     * representation of the original Tweet's ID.
     */
    @SerializedName("in_reply_to_status_id")
    public  long inReplyToStatusId;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the string
     * representation of the original Tweet's ID.
     */
    @SerializedName("in_reply_to_status_id_str")
    public  String inReplyToStatusIdStr;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the integer
     * representation of the original Tweet's author ID. This will not necessarily always be the
     * user directly mentioned in the Tweet.
     */
    @SerializedName("in_reply_to_user_id")
    public long inReplyToUserId;

    /**
     * Nullable. If the represented Tweet is a reply, this field will contain the string
     * representation of the original Tweet's author ID. This will not necessarily always be the
     * user directly mentioned in the Tweet.
     */
    @SerializedName("in_reply_to_user_id_str")
    public  String inReplyToUserIdStr;

    /**
     * Number of times this Tweet has been retweeted. This field is no longer capped at 99 and will
     * not turn into a String for "100+"
     */
    @SerializedName("retweet_count")
    public int retweetCount;

    public void incrementRtcount(){
        this.retweetCount +=1;
    }

    public void decrementRtCount(){
        this.retweetCount -=1;
    }


    /**
     * Perspectival. Indicates whether this Tweet has been retweeted by the authenticating user.
     */
    @SerializedName("retweeted")
    public boolean retweeted;

    public void setRetweet(boolean isRetweeted){
        this.retweeted = isRetweeted;
    }

    /**
     * Users can amplify the broadcast of tweets authored by other users by retweeting. Retweets can
     * be distinguished from typical Tweets by the existence of a retweeted_status attribute. This
     * attribute contains a representation of the original Tweet that was retweeted. Note that
     * retweets of retweets do not show representations of the intermediary retweet, but only the
     * original tweet. (Users can also unretweet a retweet they created by deleting their retweet.)
     */
    @SerializedName("retweeted_status")
    public  Tweet retweetedStatus;

    /**
     * Utility used to post the Tweet, as an HTML-formatted string. Tweets from the Twitter website
     * have a source value of web.
     */
    @SerializedName("source")
    public  String source;

    /**
     * The actual UTF-8 text of the status update. See twitter-text for details on what is currently
     * considered valid characters.
     */
    @SerializedName(value = "text", alternate = {"full_text"})
    public  String text;

    /**
     * The user who posted this Tweet. Perspectival attributes embedded within this object are
     * unreliable. See Why are embedded objects stale or inaccurate?.
     */
    @SerializedName("user")
    public User user;

    public Timeline(){
        //default constructor
    }

}
