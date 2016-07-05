package com.example.punit.twitterclient.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.ui.TimelineActivity;
import com.example.punit.twitterclient.util.Constants;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.File;

import retrofit.mime.TypedFile;

public class TweetUploadService extends IntentService {

    private static final String TAG = "TweetUploadService";
    String path,tweet;
    public TweetUploadService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        sendNotification();
        path = intent.getStringExtra("PATH");
        tweet = intent.getStringExtra("TWEET");
        File file = new File(path);
        TypedFile typedFile = new TypedFile("application/octet-stream",file);

        final TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        final TwitterApiClient apiClient = new TwitterApiClient(session);

        apiClient.getMediaService().upload(typedFile, null, null, new Callback<Media>() {
            @Override
            public void success(Result<Media> result) {
                apiClient.getStatusesService().update(tweet, null, null, null, null, null, null, null, result.data.mediaIdString, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        if(result.response.getStatus() == 200){
                            Log.d(TAG, "success: tweeted" );
                            successNotification();
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d(TAG, "failure: hag diya");
                        failureNotification("Error posting tweet");
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {

                Log.d(TAG, "failure: uploading " + path);
                Log.d(TAG, "failure: uploading" + exception.getLocalizedMessage());
                failureNotification("Error uploading image");
            }
        });
    }


    private void sendNotification() {
        Log.d(TAG, "sendNotification: called");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Sending Tweet")
                .setContentText(tweet)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(0,0,true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(this, TimelineActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.TWEET_NOTIF_ID,builder.build());

    }

    private void successNotification(){
        Log.d(TAG, "successNotification: called");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext())
                .setContentTitle("Tweet Sent")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(tweet)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.TWEET_NOTIF_ID,builder.build());
    }

    private void failureNotification(String reason){
        Log.d(TAG, "failureNotification: called");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Failure sending tweet")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(reason)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.TWEET_NOTIF_ID,builder.build());
    }
}
