package com.example.punit.twitterclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.adapter.TimelineAdapter;
import com.example.punit.twitterclient.model.Timeline;
import com.example.punit.twitterclient.rest.MyTwitterApiClient;
import com.example.punit.twitterclient.service.TweetUploadService;
import com.example.punit.twitterclient.service.VideoUploadService;
import com.example.punit.twitterclient.util.Constants;
import com.example.punit.twitterclient.util.ImageUtility;
import com.example.punit.twitterclient.util.MentionUtility;
import com.example.punit.twitterclient.util.UsernameTokenizer;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.client.Response;

public class ComposeTweetActivity extends AppCompatActivity {

    private static final String TAG = "ComposeTweetActivity";
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.reply_to_user) TextView reply_to_user;
    @BindView(R.id.reply_text) MultiAutoCompleteTextView reply_text;
    @BindView(R.id.image_attachment) ImageView attached_image;
    @BindView(R.id.tweet_button) Button tweet_button;
    Bundle b;
    long tweet_id = 0L;

    MyTwitterApiClient apiClient;
    String path;
    boolean tweet_with_image = false;
    boolean tweet_with_video = false;

    boolean image_attached = false;
    boolean video_attached = false;
    private String filetype;

    ArrayList<User> temp_users;
    ArrayList<String> users;
    ArrayAdapter<String> adapter;
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

        setUpMentionsListener();
    }

    public void compose(View view) {
        if (tweet_with_image) {
            composeTweetWithImage(path,tweet_id);
        }
        else if(tweet_with_video) {
            long total_bytes = new File(path).length();
            composeTweetWithVideo(path,total_bytes,filetype,tweet_id);
        }
        else {
            composeTweet(tweet_id);
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
                    String mimeType = getContentResolver().getType(data.getData());
                    filetype = mimeType;
                    if(mimeType!=null && mimeType.contains("image/")){
                        image_attached = true;
                        video_attached = false;
                    }
                    else if(mimeType!=null && mimeType.contains("video/mp4")){
                        video_attached = true;
                        image_attached = false;
                    }
                    else{
                        Toast.makeText(ComposeTweetActivity.this,"This format is not supported",Toast.LENGTH_SHORT).show();
                    }
                    try {
                        Bitmap bm;
                        path = ImageUtility.getPath(this,data.getData());
                        Log.d(TAG, "onActivityResult:Image path " + data.getData() + " " + path);
                        if(image_attached) {
                            Log.d(TAG, "onActivityResult: image path");
                            bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                            attached_image.setImageBitmap(bm);
                            tweet_with_image = true;
                            tweet_with_video = false;
                        }
                        else if(video_attached){
                            Log.d(TAG, "onActivityResult: video path");
                            bm = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
                            attached_image.setImageBitmap(bm);
                            tweet_with_video = true;
                            tweet_with_image = false;
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onActivityResult: " + e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    private void composeTweet(long id) {
        apiClient.getCustomService().postTweet(reply_text.getText().toString(),
                id,
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


    private void composeTweetWithImage(String file_path,long id) {
        tweet_button.setEnabled(false);
        Toast.makeText(ComposeTweetActivity.this, "Posting Tweet!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ComposeTweetActivity.this, TweetUploadService.class);
        intent.putExtra("PATH", file_path);
        intent.putExtra("TWEET", reply_text.getText().toString());
        if(id > 0){
            intent.putExtra("REPLY_ID",id);
        }
        startService(intent);
        finish();
    }

    private void composeTweetWithVideo(String file_path,long total_bytes,String file_type,long id){
        tweet_button.setEnabled(false);
        Toast.makeText(ComposeTweetActivity.this,"Posting Tweet!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ComposeTweetActivity.this, VideoUploadService.class);
        intent.putExtra("PATH",file_path);
        intent.putExtra("TWEET",reply_text.getText().toString());
        intent.putExtra("SIZE",total_bytes);
        intent.putExtra("TYPE",file_type);
        if(id > 0){
            intent.putExtra("REPLY_ID",id);
        }
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

    private void setUpMentionsListener(){
        temp_users = new ArrayList<>();
        users = new ArrayList<>();
        reply_text.setTokenizer(new UsernameTokenizer());
        reply_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int cursor = start;
                if (cursor >= s.length()) cursor = s.length()-1;
                if (MentionUtility.isValidToken(s, cursor)){
                    Log.d(TAG, "onTextChanged: called");
                    String token = MentionUtility.getToken(s, start);
                    apiClient.getCustomService().searchUsers(token, false, new Callback<List<User>>() {
                        @Override
                        public void success(Result<List<User>> result) {
                            temp_users.clear();
                            temp_users = new ArrayList<>(result.data);
                            users.clear();
                             for(User user:temp_users){
                                 users.add(user.screenName);
                             }
                            adapter = new ArrayAdapter<>(ComposeTweetActivity.this,android.R.layout.simple_dropdown_item_1line,users);
                            reply_text.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(TwitterException exception) {

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}
