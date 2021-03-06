package com.example.punit.twitterclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.adapter.ReplyAdapter;
import com.example.punit.twitterclient.adapter.TimelineAdapter;
import com.example.punit.twitterclient.model.Timeline;
import com.example.punit.twitterclient.rest.MyTwitterApiClient;
import com.example.punit.twitterclient.util.CheckableImageView;
import com.example.punit.twitterclient.util.Constants;
import com.example.punit.twitterclient.util.DividerItemDecoration;
import com.example.punit.twitterclient.util.Utility;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailTweetActivity extends AppCompatActivity{

    Toolbar toolbar;
    TextView page_title;

    @BindView(R.id.detail_tweet_profile_img) ImageView profile_image;
    @BindView(R.id.detail_tweet_username) TextView user_name;
    @BindView(R.id.detail_tweet_twitter_name) TextView twitter_name;
    @BindView(R.id.detail_tweet_complete_tweet) TextView tweet;
    @BindView(R.id.detail_tweet_time) TextView timestamp;
    @BindView(R.id.retweets_count) TextView retweets_count;
    @BindView(R.id.likes_count) TextView likes_count;
    @BindView(R.id.reply_btn) ImageView reply;
    @BindView(R.id.retweet_btn) CheckableImageView retweet;
    @BindView(R.id.like_btn) CheckableImageView like;

    //Reply List
    @BindView(R.id.reply_rv) RecyclerView reply_rv;
    LinearLayoutManager linearLayoutManager;
    ReplyAdapter adapter;

    MyTwitterApiClient apiClient;
    long tweet_id;
    long retweet_id;

    String original_username;

    int fav_count,retweet_count;
    boolean fav_status;
    boolean retweet_status;
    boolean fav_status_changed = false;
    boolean retweet_status_changed = false;
    int position;

    Bundle b;
    ArrayList<String> users;
    private static final String TAG = "DetailTweetActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        page_title = (TextView) toolbar.findViewById(R.id.page_title);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        page_title.setText(getString(R.string.detail_tweet_title));

        b = getIntent().getExtras();

        tweet_id = b.getLong(Constants.BTWEET_ID_STR,0L);
        retweet_id = b.getLong(Constants.BRT_ID_STR,0L);
        position = b.getInt(Constants.BPOSITION);
        original_username = b.getString(Constants.BORIGINAL_USER_NAME,null);

        //setting up profile image
        Picasso.with(this)
                .load(b.getString(Constants.BPROFILE_IMG_URL))
                .placeholder(R.drawable.profile_image_placeholder)
                .into(profile_image);

        //setting up username,twitter_name and tweet
        user_name.setText(b.getString(Constants.BUSERNAME,""));
        twitter_name.setText(getString(R.string.detail_tweet_twitter_name_at_annotation,b.getString(Constants.BTWITTERNAME,"")));
        tweet.setText(b.getString(Constants.BTWEET,""));
        Utility.hashTagsAndLinks(tweet);

        //Setting up retweets and likes count
        retweet_count = b.getInt(Constants.BRETWEETS,0);
        fav_count = b.getInt(Constants.BLIKES,0);
        Log.d(TAG, "onCreate: " + String.valueOf(fav_count));
        retweets_count.setText(getString(R.string.detail_tweet_retweets_count,retweet_count));
        likes_count.setText(getString(R.string.detail_tweet_favs_count,fav_count));

        //Show Retweeted and Liked status by icons
        fav_status = b.getBoolean(Constants.BFAVORITED,false);
        retweet_status = b.getBoolean(Constants.BRETWEETED,false);
        if(fav_status) {
            like.setChecked(true);
        }
        if(retweet_status) {
            retweet.setChecked(true);
        }

        users = b.getStringArrayList(Constants.CMENTIONS);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reply_rv.setLayoutManager(linearLayoutManager);
        reply_rv.addItemDecoration(new DividerItemDecoration(DetailTweetActivity.this,R.drawable.item_divider));


        //Setting up Client
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        apiClient = new MyTwitterApiClient(session);

        if(retweet_id>0 && original_username!=null) {
            showRepliesToTweet(retweet_id,"@" + original_username);
        }
        else{
            showRepliesToTweet(tweet_id,twitter_name.getText().toString());
        }

    }


    public void retweet(View view){
        retweet_status_changed = true;
        if(!retweet.isChecked()){
            retweet.setChecked(true);
            apiClient.getCustomService().retweetTweet(tweet_id, new Callback<Response>() {
                @Override
                public void success(Result<Response> result) {
                    if(result.response.getStatus() == 200){
                        Toast.makeText(DetailTweetActivity.this,getString(R.string.detail_tweet_retweet_complete),Toast.LENGTH_SHORT).show();
                        retweet_status = true;
                        retweet_count +=1;
                        retweets_count.setText(getString(R.string.detail_tweet_retweets_count,retweet_count));
                    }
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(DetailTweetActivity.this,exception.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    retweet.setChecked(false);
                    retweet_status = false;
                }
            });
        }
        else{
            retweet.setChecked(false);
            apiClient.getCustomService().unretweetTweet(tweet_id, new Callback<Response>() {
                @Override
                public void success(Result<Response> result) {
                    Toast.makeText(DetailTweetActivity.this,getString(R.string.detail_tweet_unretweet_complete),Toast.LENGTH_SHORT).show();
                    retweet_status = false;
                    retweet_count -=1;
                    retweets_count.setText(getString(R.string.detail_tweet_retweets_count,retweet_count));
                }

                @Override
                public void failure(TwitterException exception) {
                    if(exception.getLocalizedMessage().contains("429")){
                        Toast.makeText(DetailTweetActivity.this,getString(R.string.detail_tweet_retweet_btn_violation),Toast.LENGTH_SHORT).show();
                    }
                    retweet.setChecked(true);
                    retweet_status = true;
                }
            });
        }
    }

    public void like(View view){
        fav_status_changed = true;
        if(like.isChecked()){
            //unfavorite tweet
            like.setChecked(false);
            apiClient.getCustomService().unfavoriteTweet(tweet_id, new Callback<Response>() {
                @Override
                public void success(Result<Response> result) {
                    if(result.response.getStatus() == 200){
                        Toast.makeText(DetailTweetActivity.this,"UNLIKED!",Toast.LENGTH_SHORT).show();
                        fav_status = false;
                        fav_count -=1;
                        Log.d(TAG, "success: unfavorited" + String.valueOf(fav_count));
                        likes_count.setText(getString(R.string.detail_tweet_favs_count,fav_count));
                    }
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(DetailTweetActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    like.setChecked(true);
                    fav_status = true;
                }
            });
        }
        else {
            //favorite tweet
            like.setChecked(true);
            apiClient.getCustomService().favoriteTweet(tweet_id, new Callback<Response>() {
                @Override
                public void success(Result<Response> result) {
                    if (result.response.getStatus() == 200) {
                        Toast.makeText(DetailTweetActivity.this, "LIKED!!", Toast.LENGTH_SHORT).show();
                        fav_status = true;
                        fav_count +=1;
                        Log.d(TAG, "success: favorited " + String.valueOf(fav_count));
                        likes_count.setText(getString(R.string.detail_tweet_favs_count,fav_count));
                    }
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(DetailTweetActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    like.setChecked(false);
                    fav_status = false;
                }
            });
        }
    }

    public void reply(View view) {
        Intent reply_intent = new Intent(DetailTweetActivity.this, ComposeTweetActivity.class);
        Bundle b = new Bundle();
        b.putString(Constants.CUSER_NAME,user_name.getText().toString());
        if(users!=null && users.size()>0){
            b.putStringArrayList(Constants.CMENTIONS,users);
        }
        b.putString(Constants.CTWITTER_NAME, twitter_name.getText().toString());
        if(retweet_id > 0) {
            b.putLong(Constants.CTWEET_ID, retweet_id);
        }
        else{
            b.putLong(Constants.CTWEET_ID,tweet_id);
        }
        reply_intent.putExtras(b);
        startActivity(reply_intent);
        overridePendingTransition(R.anim.modal_activity_open_enter,R.anim.modal_activity_close_exit);
    }


    private void showRepliesToTweet(final long t_id,String screen_name){
        Log.d(TAG, "showRepliesToTweet: " + String.valueOf(t_id));
        apiClient.getCustomService().searchTweets("to:" + screen_name,
                t_id,
                new Callback<Search>() {
                    @Override
                    public void success(Result<Search> result) {
                        ArrayList<Tweet> tweets =  new ArrayList<>(result.data.tweets);
                        Log.d(TAG, "success: " + String.valueOf(tweets.size()));
                        for (Tweet tweet:tweets){
                            if(tweet.inReplyToStatusId == t_id) {
                                Log.d(TAG, "success: reply_to_id " + String.valueOf(tweet.inReplyToStatusId));
                                Log.d(TAG, "success: tweet_txt: " + tweet.text);
                                adapter = new ReplyAdapter(DetailTweetActivity.this,tweets);
                                reply_rv.setAdapter(adapter);
                            }
                        }

                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d(TAG, "failure: " + exception.getLocalizedMessage());
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(fav_status_changed || retweet_status_changed) {
                    sendResult();
                }
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(fav_status_changed || retweet_status_changed) {
            sendResult();
        }
        super.onBackPressed();

    }

    private void sendResult(){
        Intent intent = new Intent();
        intent.putExtra(Constants.POSITION,position);
        if(fav_status_changed && retweet_status_changed){
            Log.d(TAG, "sendResult: Both changed");
            intent.putExtra(Constants.CHANGE_ID,Constants.BOTH_CHANGED_ID);
            intent.putExtra(Constants.FAV_STATUS,fav_status);
            intent.putExtra(Constants.RT_STATUS,retweet_status);
            intent.putExtra(Constants.FAV_COUNT,fav_count);
            intent.putExtra(Constants.RT_COUNT,retweet_count);
        }
        else if(fav_status_changed){
            Log.d(TAG, "sendResult: Fav changed");
            intent.putExtra(Constants.CHANGE_ID,Constants.FAV_CHANGED_ID);
            intent.putExtra(Constants.FAV_STATUS,fav_status);
            intent.putExtra(Constants.FAV_COUNT,fav_count);
        }
        else{
            Log.d(TAG, "sendResult: RT Changed");
            intent.putExtra(Constants.CHANGE_ID,Constants.RT_CHANGED_ID);
            intent.putExtra(Constants.RT_STATUS,retweet_status);
            intent.putExtra(Constants.RT_COUNT,retweet_count);
        }
        setResult(Activity.RESULT_OK,intent);
        finish();
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
