package com.example.punit.twitterclient.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.rest.MyTwitterApiClient;
import com.example.punit.twitterclient.service.TweetUploadService;
import com.example.punit.twitterclient.util.Constants;
import com.example.punit.twitterclient.util.ImageUtility;
import com.example.punit.twitterclient.util.Utility;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class ComposeTweetActivity extends AppCompatActivity {

    private static final String TAG = "ComposeTweetActivity";
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.reply_to_user) TextView reply_to_user;
    @BindView(R.id.reply_text) EditText reply_text;
    @BindView(R.id.image_attachment) ImageView attached_image;
    @BindView(R.id.tweet_button) Button tweet_button;
    Bundle b;
    long tweet_id;

    MyTwitterApiClient apiClient;
    String photo_path;
    boolean tweet_with_image = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        toolbar.setNavigationIcon(R.drawable.ic_cc_nav_dismiss);

        b = getIntent().getExtras();
        if (b.getBoolean(Constants.OPEN_COMPOSE, false)) {
            hideReplyToUserText();
        }
        else{
            reply_to_user.setText("In reply to " + b.getString(Constants.CUSER_NAME, ""));
            if (b.getStringArrayList(Constants.CMENTIONS) != null) {
                ArrayList<String> users = b.getStringArrayList(Constants.CMENTIONS);
                for (String s : users) {
                    reply_text.append(s + " ");
                }
                reply_text.append(b.getString(Constants.CTWITTER_NAME, "") + " ");
            } else {
                reply_text.append(b.getString(Constants.CTWITTER_NAME, "") + " ");
            }

            tweet_id = b.getLong(Constants.CTWEET_ID, 0);
        }

        //setting up client
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        apiClient = new MyTwitterApiClient(session);
    }

    public void compose(View view) {
        if (b.getBoolean(Constants.OPEN_COMPOSE, false)) {
            if (tweet_with_image) {
                composeTweetWithImage(photo_path);
            } else {
                composeTweet();
            }
        } else {
            if (tweet_with_image) {
                replyToTweetWithImage(photo_path);
            } else {
                replyToTweet(tweet_id);
            }
        }

    }


    public void attachImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/* video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.IMAGE_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.IMAGE_REQ_CODE) {
                if (data != null) {
                    try {
                        photo_path = ImageUtility.getPath(this,data.getData());
                        Log.d(TAG, "onActivityResult:Image path " + data.getData() + photo_path);
                        Bitmap bm;
                        bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        attached_image.setImageBitmap(bm);
                        tweet_with_image = true;
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onActivityResult: " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }



    private void replyToTweet(long id) {
        apiClient.getCustomService().replyToTweet(reply_text.getText().toString(),
                id,
                new Callback<Response>() {
                    @Override
                    public void success(Result<Response> result) {
                        if (result.response.getStatus() == 200) {
                            Toast.makeText(ComposeTweetActivity.this, "Replied successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(ComposeTweetActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void replyToTweetWithImage(String filePath) {
        //Implementation coming soon..
        Toast.makeText(ComposeTweetActivity.this,"Coming soon.",Toast.LENGTH_SHORT).show();
    }

    private void composeTweet() {
        apiClient.getCustomService().postTweet(reply_text.getText().toString(),
                new Callback<Response>() {
                    @Override
                    public void success(Result<Response> result) {
                        if (result.response.getStatus() == 200) {
                            Toast.makeText(ComposeTweetActivity.this, "Tweet posted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Toast.makeText(ComposeTweetActivity.this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void composeTweetWithImage(String file_path) {
        tweet_button.setEnabled(false);
        Toast.makeText(ComposeTweetActivity.this, "Posting Tweet!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ComposeTweetActivity.this, TweetUploadService.class);
        intent.putExtra("PATH", file_path);
        intent.putExtra("TWEET", reply_text.getText().toString());
        startService(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void hideReplyToUserText() {
        reply_to_user.setVisibility(View.GONE);
        reply_text.setHint(getString(R.string.compose_hint_text));
    }


}
