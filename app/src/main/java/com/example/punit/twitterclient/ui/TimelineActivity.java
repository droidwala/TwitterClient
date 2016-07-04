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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.adapter.TimelineAdapter;
import com.example.punit.twitterclient.model.Timeline;
import com.example.punit.twitterclient.rest.MyTwitterApiClient;
import com.example.punit.twitterclient.util.ClickListener;
import com.example.punit.twitterclient.util.Constants;
import com.example.punit.twitterclient.util.DividerItemDecoration;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.MentionEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TimelineActivity extends AppCompatActivity implements ClickListener{

    //Toolbar items
    Toolbar toolbar;
    TextView page_title;

    //Views
    @BindView(R.id.timeline_rv) RecyclerView timeline_rv;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    LinearLayoutManager linearLayoutManager;
    TimelineAdapter adapter;
    ArrayList<Timeline> tweets;

    //variables used to load more tweets using pagination
    boolean isLoading = false;
    long max_id;

    //Use to make REST API calls
    MyTwitterApiClient apiClient;
    TwitterSession session;

    private static final String TAG = "TimelineActivity";
    private static final int REQ_CODE = 99;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        page_title = (TextView) toolbar.findViewById(R.id.page_title);
        setSupportActionBar(toolbar);
        page_title.setText(getResources().getString(R.string.timeline_title));

        //Get Active session object to be use to make REST API calls for logged in user

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        apiClient = new MyTwitterApiClient(session);
        //Setting up Layout Manager & Recyclerview
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        timeline_rv.setLayoutManager(linearLayoutManager);
        timeline_rv.setHasFixedSize(true);
        timeline_rv.addItemDecoration(new DividerItemDecoration(TimelineActivity.this,R.drawable.item_divider));
        timeline_rv.addOnScrollListener(recyclerViewScrollListener);

        fetchTweets(Constants.TWEET_COUNT);

    }


    private void fetchTweets(int count){
        progressBar.setVisibility(View.VISIBLE);
        apiClient.getCustomService().showTimeline(count, new Callback<List<Timeline>>() {
            @Override
            public void success(Result<List<Timeline>> result) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                tweets = new ArrayList<>(result.data);
                if(tweets.size()>0){
                    max_id = (tweets.get(tweets.size()-1).id) - 1L;
                    adapter = new TimelineAdapter(TimelineActivity.this,tweets);
                    adapter.setClickListener(TimelineActivity.this);
                    timeline_rv.setAdapter(adapter);
                }
                else{
                    Toast.makeText(TimelineActivity.this,R.string.no_tweets_fetched,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(TwitterException exception) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "failure: " + exception.getMessage());
                Toast.makeText(TimelineActivity.this,exception.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchMoreTweets(int count,long id){
        apiClient.getCustomService().showMoreTimeline(count,id, new Callback<List<Timeline>>() {
            @Override
            public void success(Result<List<Timeline>> result) {
                adapter.removeLoading();
                isLoading = false;
                ArrayList<Timeline> tweets = new ArrayList<>(result.data);
                max_id = (tweets.get(tweets.size()-1).id) - 1;
                Log.d(TAG, "success: more items added " + String.valueOf(max_id));
                adapter.addAll(tweets);
            }

            @Override
            public void failure(TwitterException exception) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "failure: " + exception.getMessage());
                Toast.makeText(TimelineActivity.this,exception.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Custom Recyclerview Scroll listener to handle loading of more tweets when we reach end of our list.
     */
    private RecyclerView.OnScrollListener recyclerViewScrollListener =
            new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visibleCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                    if (max_id > 0 && !isLoading) {
                        if ((visibleCount + firstVisibleItemPosition) >= totalItemCount) {
                            adapter.addLoading();//adds dummy view to show progress bar
                            isLoading = true; //prevents duplicate rest api calls being made
                            fetchMoreTweets(Constants.TWEET_COUNT, max_id);

                        }
                    }
                }
            };


    @Override
    public void itemClicked(View view, int position) {
        Log.d(TAG, "itemClicked: " + String.valueOf(position));
        Intent intent = new Intent(TimelineActivity.this,DetailTweetActivity.class);
        Bundle b = new Bundle();
        b.putInt(Constants.BPOSITION,position);
        b.putString(Constants.BTWEET_ID_STR,tweets.get(position).idStr);
        b.putString(Constants.BPROFILE_IMG_URL,tweets.get(position).user.profileImageUrl);
        b.putString(Constants.BUSERNAME,tweets.get(position).user.name);
        b.putString(Constants.BTWITTERNAME,tweets.get(position).user.screenName);
        b.putString(Constants.BTWEET,tweets.get(position).text);
        List<MentionEntity> user_mentions = tweets.get(position).entities.userMentions;
        if(user_mentions.size()>0){
            int length = user_mentions.size();
            ArrayList<String> user_names = new ArrayList<>();
            for(int i=0;i<length;i++){
                user_names.add("@" + user_mentions.get(i).screenName);
            }
            b.putStringArrayList(Constants.CMENTIONS,user_names);
        }
        if(tweets.get(position).retweetedStatus!=null) {
            b.putInt(Constants.BRETWEETS, tweets.get(position).retweetedStatus.retweetCount);
            b.putInt(Constants.BLIKES, tweets.get(position).retweetedStatus.favoriteCount);
        }
        else{
            b.putInt(Constants.BRETWEETS,tweets.get(position).retweetCount);
            b.putInt(Constants.BLIKES,tweets.get(position).favoriteCount);
        }
        b.putBoolean(Constants.BRETWEETED,tweets.get(position).retweeted);
        b.putBoolean(Constants.BFAVORITED,tweets.get(position).favorited);
        intent.putExtras(b);
        startActivityForResult(intent,REQ_CODE);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: outside if condition");
        if(requestCode == REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: inside if condition");
                int position = data.getIntExtra(Constants.POSITION, -1);
                int change_id = data.getIntExtra(Constants.CHANGE_ID, -1);
                boolean fav_status, rt_status;
                int fav_count,rt_count;
                if (position > 0) {
                    Log.d(TAG, "onActivityResult: " + String.valueOf(position));
                    switch (change_id) {
                        case Constants.BOTH_CHANGED_ID:
                            Log.d(TAG, "onActivityResult: Both changed" );
                            fav_status = data.getBooleanExtra(Constants.FAV_STATUS, false);
                            rt_status = data.getBooleanExtra(Constants.RT_STATUS, false);
                            tweets.get(position).setfavorite(fav_status);
                            tweets.get(position).setRetweet(rt_status);
                            fav_count = data.getIntExtra(Constants.FAV_COUNT,0);
                            rt_count = data.getIntExtra(Constants.RT_COUNT,0);
                            changeFavCounter(position,fav_count);
                            changeRtCounter(position,rt_count);
                            break;

                        case Constants.FAV_CHANGED_ID:
                            Log.d(TAG, "onActivityResult: Fav changed");
                            fav_status = data.getBooleanExtra(Constants.FAV_STATUS, false);
                            tweets.get(position).setfavorite(fav_status);
                            fav_count  = data.getIntExtra(Constants.FAV_COUNT,0);
                            changeFavCounter(position,fav_count);
                            break;

                        case Constants.RT_CHANGED_ID:
                            Log.d(TAG, "onActivityResult: Rt changed");
                            rt_status = data.getBooleanExtra(Constants.RT_STATUS,false);
                            tweets.get(position).setRetweet(rt_status);
                            rt_count = data.getIntExtra(Constants.RT_COUNT,0);
                            changeRtCounter(position,rt_count);
                            break;
                        default:
                            break;
                    }

                }
            }
        }
    }

    private void changeRtCounter(int position,int rt_count){
        if(tweets.get(position).retweetedStatus!=null) {
            tweets.get(position).retweetedStatus.setRtCount(rt_count);
        }
        else{
            tweets.get(position).setRtCount(rt_count);
        }
    }

    private void changeFavCounter(int position,int fav_count){
        if(tweets.get(position).retweetedStatus!=null){
            tweets.get(position).retweetedStatus.setFavoriteCount(fav_count);
        }
        else{
            tweets.get(position).setFavoriteCount(fav_count);
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
