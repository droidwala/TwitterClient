package com.example.punit.twitterclient.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.rest.MyTwitterApiClient;
import com.example.punit.twitterclient.util.Constants;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class ComposeTweetActivity extends AppCompatActivity{

    private static final String TAG = "ComposeTweetActivity";
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.reply_to_user) TextView reply_to_user;
    @BindView(R.id.reply_text) EditText reply_text;
    Bundle b;
    long tweet_id;

    MyTwitterApiClient apiClient;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        toolbar.setNavigationIcon(R.drawable.ic_cc_nav_dismiss);


        b = getIntent().getExtras();

        reply_to_user.setText("In reply to " + b.getString(Constants.CUSER_NAME,""));
        reply_text.append(b.getString(Constants.CTWITTER_NAME,""));
        tweet_id = b.getLong(Constants.CTWEET_ID,0);


        //setting up client
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        apiClient = new MyTwitterApiClient(session);

    }

    public void replyToTweet(View view){
        apiClient.getCustomService().replyToTweet(reply_text.getText().toString(),
                tweet_id,
                new Callback<Response>() {
                    @Override
                    public void success(Result<Response> result) {
                        if(result.response.getStatus() == 200){
                            Toast.makeText(ComposeTweetActivity.this,"Replied successfully",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {
                         Toast.makeText(ComposeTweetActivity.this,exception.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
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
        return super.onOptionsItemSelected(item);
    }
}
