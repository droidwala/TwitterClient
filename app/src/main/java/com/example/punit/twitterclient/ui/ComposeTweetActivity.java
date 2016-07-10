package com.example.punit.twitterclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.rest.MyTwitterApiClient;
import com.example.punit.twitterclient.service.TweetUploadService;
import com.example.punit.twitterclient.service.VideoUploadService;
import com.example.punit.twitterclient.util.BitmapUtility;
import com.example.punit.twitterclient.util.Constants;
import com.example.punit.twitterclient.util.ImageUtility;
import com.example.punit.twitterclient.util.MentionUtility;
import com.example.punit.twitterclient.util.PermissionUtility;
import com.example.punit.twitterclient.util.UsernameTokenizer;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.io.File;
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
    @BindView(R.id.parent_layout_compose) LinearLayout parent_layout;
    Bundle b;
    long tweet_id = 0L;

    MyTwitterApiClient apiClient;
    String path;
    boolean image_attached = false;
    boolean video_attached = false;
    boolean gif_attached = false;
    private String filetype;

    ArrayList<User> temp_users;
    ArrayList<String> users;
    ArrayAdapter<String> adapter;
    Bitmap bm;
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
        if (image_attached) {
            composeTweetWithImage(path,tweet_id);
        }
        else if(video_attached || gif_attached) {
            long total_bytes = new File(path).length();
            composeTweetWithVideo(path,total_bytes,filetype,tweet_id);
        }
        else {
            composeTweet(tweet_id);
        }

    }


    public void attachImage(View view) {
        if(PermissionUtility.checkIfStoragePermissionIsGranted(this)){
            Intent intent = new Intent();
            intent.setType("image/* video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.IMAGE_REQ_CODE);
        }
        else{
            PermissionUtility.requestStoragePermission(this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.IMAGE_REQ_CODE) {
                if (data != null) {
                    path = ImageUtility.getPath(this,data.getData()).toLowerCase();
                    Log.d(TAG, "PATH: " +  path);
                    if(path!=null && ((path.contains(".jpg"))
                            || (path.contains(".png"))
                            || (path.contains(".webp")))){
                        image_attached = true;
                        video_attached = false;
                        gif_attached = false;
                        filetype = "image/" + path.substring(path.lastIndexOf(".") + 1,path.length());
                    }
                    else if(path!=null && path.contains(".gif")){
                        image_attached = false;
                        video_attached = false;
                        gif_attached = true;
                        filetype = "image/" + path.substring(path.lastIndexOf(".") + 1,path.length());
                    }
                    else if(path!=null && path.contains(".mp4")){
                        video_attached = true;
                        image_attached = false;
                        gif_attached = false;
                        filetype = "video/" + path.substring(path.lastIndexOf(".") + 1,path.length());
                    }
                    else{
                        Toast.makeText(ComposeTweetActivity.this,"This format is not supported",Toast.LENGTH_SHORT).show();
                    }

                        if(image_attached || gif_attached) {
                            Log.d(TAG, "Image/Gif attached");
                            attached_image.setImageBitmap(BitmapUtility.decodeSampledBitmapFromResource(path,200,200));
                        }
                        else if(video_attached){
                            Log.d(TAG, "Video attached");
                            bm = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
                            attached_image.setImageBitmap(bm);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constants.EXT_STORAGE_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent();
                    intent.setType("image/* video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.IMAGE_REQ_CODE);
                }
                else{
                    Snackbar snackbar = Snackbar.make(parent_layout,
                            "App needs storage permission for you to able to attach video/image in your tweets",
                            Snackbar.LENGTH_LONG)
                            .setAction("Settings", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",getPackageName(),null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            })
                            .setActionTextColor(ContextCompat.getColor(this,R.color.color_yellow));

                    View snackbarview = snackbar.getView();
                    snackbarview.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

                    snackbar.show();


                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
