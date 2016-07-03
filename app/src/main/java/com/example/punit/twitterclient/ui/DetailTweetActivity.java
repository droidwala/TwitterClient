package com.example.punit.twitterclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.rest.MyTwitterApiClient;
import com.example.punit.twitterclient.util.CheckableImageView;
import com.example.punit.twitterclient.util.Constants;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.client.Response;

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

    MyTwitterApiClient apiClient;
    long tweet_id;

    boolean fav_status;
    int position;

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

        Bundle b = getIntent().getExtras();

        tweet_id = Long.parseLong(b.getString(Constants.BTWEET_ID_STR));
        position = b.getInt(Constants.BPOSITION);
        Picasso.with(this)
                .load(b.getString(Constants.BPROFILE_IMG_URL))
                .placeholder(R.drawable.profile_image_placeholder)
                .into(profile_image);

        user_name.setText(b.getString(Constants.BUSERNAME,""));
        String twittername = "@" + b.getString(Constants.BTWITTERNAME,"");
        twitter_name.setText(twittername);
        tweet.setText(b.getString(Constants.BTWEET,""));
        String retweets = String.valueOf(b.getInt(Constants.BRETWEETS,0)) + " " + getString(R.string.detail_tweet_retweets_str);
        retweets_count.setText(retweets);
        String likes = String.valueOf(b.getInt(Constants.BLIKES,0)) + " " + getString(R.string.detail_tweet_likes_str);
        likes_count.setText(likes);

        fav_status = b.getBoolean(Constants.BFAVORITED,false);


        if(fav_status) {
            like.setChecked(true);
        }
        if(b.getBoolean(Constants.BRETWEETED,false)) {
            retweet.setChecked(true);
        }

        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        apiClient = new MyTwitterApiClient(session);
    }


    public void retweet(View view){
        retweet.setChecked(true);
        //Toast.makeText(DetailTweetActivity.this,"Retweeted!",Toast.LENGTH_SHORT).show();
    }

    public void like(View view){
        if(like.isChecked()){
            //unfavorite tweet
            like.setChecked(false);
            apiClient.getCustomService().unfavoriteTweet(tweet_id, new Callback<Response>() {
                @Override
                public void success(Result<Response> result) {
                    if(result.response.getStatus() == 200){
                        Toast.makeText(DetailTweetActivity.this,"UNLIKED!",Toast.LENGTH_SHORT).show();
                        fav_status = false;
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(Constants.FAV_STATUS,fav_status);
                intent.putExtra(Constants.POSITION,position);
                setResult(Activity.RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.FAV_STATUS,fav_status);
        intent.putExtra(Constants.POSITION,position);
        setResult(Activity.RESULT_OK,intent);
        super.onBackPressed();

    }
}
