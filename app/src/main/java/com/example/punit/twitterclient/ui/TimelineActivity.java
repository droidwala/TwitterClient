package com.example.punit.twitterclient.ui;

import android.os.Bundle;
import android.os.Handler;
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
import com.example.punit.twitterclient.rest.MyTwitterApiClient;
import com.example.punit.twitterclient.util.ClickListener;
import com.example.punit.twitterclient.util.Constants;
import com.example.punit.twitterclient.util.DividerItemDecoration;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TimelineActivity extends AppCompatActivity implements ClickListener{

    //Toolbar items
    Toolbar toolbar;
    TextView page_title;

    //Views
    @BindView(R.id.timeline_rv) RecyclerView timeline_rv;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    LinearLayoutManager linearLayoutManager;
    TimelineAdapter adapter;
    ArrayList<Tweet> tweets;

    //variables used to load more tweets using pagination
    boolean isLoading = false;
    long max_id;

    //Use to make REST API calls
    MyTwitterApiClient apiClient;
    TwitterSession session;

    private static final String TAG = "TimelineActivity";

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
        apiClient.getCustomService().showTimeline(count, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                tweets = new ArrayList<>(result.data);
                if(tweets.size()>0){
                    max_id = (tweets.get(tweets.size()-1).getId()) - 1L;
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
        apiClient.getCustomService().showMoreTimeline(count,id, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                adapter.removeLoading();
                isLoading = false;
                ArrayList<Tweet> tweets = new ArrayList<>(result.data);
                max_id = (tweets.get(tweets.size()-1).getId()) - 1;
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
    }
}
