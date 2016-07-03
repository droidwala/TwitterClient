package com.example.punit.twitterclient.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.util.CheckableImageView;
import com.example.punit.twitterclient.util.Constants;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;

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


        if(b.getBoolean(Constants.BFAVORITED,false)) {
            like.setChecked(true);
        }
        if(b.getBoolean(Constants.BRETWEETED,false)) {
            retweet.setChecked(true);
        }

    }


    public void retweet(View view){
        retweet.setChecked(true);
        Toast.makeText(DetailTweetActivity.this,"Retweeted!",Toast.LENGTH_SHORT).show();
    }

    public void like(View view){
        like.setChecked(true);
        Toast.makeText(DetailTweetActivity.this,"Liked!",Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return false;
    }
}
