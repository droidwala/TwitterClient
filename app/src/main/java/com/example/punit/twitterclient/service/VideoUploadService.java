package com.example.punit.twitterclient.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.rest.ChunkTwitterApiClient;
import com.example.punit.twitterclient.rest.MyTwitterApiClient;
import com.example.punit.twitterclient.util.Constants;
import com.example.punit.twitterclient.util.NotificationUtility;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Media;

import java.io.File;

import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class VideoUploadService extends IntentService {

    private static final String TAG = "VideoUploadService";

    String path,file_type,tweet;
    long file_size;
    String media_id;
    ChunkTwitterApiClient apiClient;
    MyTwitterApiClient statusClient;
    TwitterSession session;
    public VideoUploadService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        tweet = intent.getStringExtra("TWEET");
        path = intent.getStringExtra("PATH");
        file_type = intent.getStringExtra("TYPE");
        file_size = intent.getLongExtra("SIZE",0L);

        NotificationUtility.sendNotification(this,tweet, Constants.TWEET_VIDEO_NOTIF_ID);

        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        apiClient = new ChunkTwitterApiClient(session);

        uploadINIT();

    }

    private void uploadINIT(){
        apiClient.getCustomService().uploadINIT("INIT", file_type, file_size, new Callback<Media>() {
            @Override
            public void success(Result<Media> result) {
                media_id = result.data.mediaIdString;
                Log.d(TAG, "success: " + media_id);
                uploadAPPEND();

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "failure: INIT " + exception.getLocalizedMessage());
                NotificationUtility.failureNotification(VideoUploadService.this,getString(R.string.error_uploading_tweet),Constants.TWEET_VIDEO_NOTIF_ID);
            }
        });
    }

    private void uploadAPPEND(){
        File file = new File(path);
        final TypedFile typedFile = new TypedFile("application/octet-stream",file);

        apiClient.getCustomService().uploadAPPEND("APPEND",media_id, typedFile, 0,
                new Callback<Response>() {
                    @Override
                    public void success(Result<Response> result) {
                        uploadFINALIZE();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d(TAG, "failure: APPEND " + exception.getLocalizedMessage());
                        NotificationUtility.failureNotification(VideoUploadService.this,getString(R.string.error_uploading_tweet),Constants.TWEET_VIDEO_NOTIF_ID);
                    }
                });
    }

    private void uploadFINALIZE(){
        apiClient.getCustomService().uploadFINALIZE("FINALIZE", media_id, new Callback<Media>() {
            @Override
            public void success(Result<Media> result) {
                uploadTWEET(result.data.mediaIdString);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "failure: FINALIZE " + exception.getLocalizedMessage());
                NotificationUtility.failureNotification(VideoUploadService.this,getString(R.string.error_uploading_tweet),Constants.TWEET_VIDEO_NOTIF_ID);
            }
        });

    }

    private void uploadTWEET(String id){
        statusClient = new MyTwitterApiClient(session);
        statusClient.getCustomService().postTweetWithVideo(tweet, id, new Callback<Response>() {
            @Override
            public void success(Result<Response> result) {
                if(result.data.getStatus() == 200){
                    NotificationUtility.successNotification(VideoUploadService.this,tweet,Constants.TWEET_VIDEO_NOTIF_ID);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "failure: STATUS " + exception.getLocalizedMessage());
                NotificationUtility.failureNotification(VideoUploadService.this,getString(R.string.error_uploading_tweet),Constants.TWEET_VIDEO_NOTIF_ID);
            }
        });
    }



}
