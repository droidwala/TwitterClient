package com.example.punit.twitterclient.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.ui.TimelineActivity;

public class NotificationUtility {


    private static final String TAG = "NotificationUtility";
    public static void sendNotification(Context context,String tweet,int NOTIF_ID) {
        Log.d(TAG, "sendNotification: called");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Sending Tweet")
                .setContentText(tweet)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(0,0,true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(context, TimelineActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIF_ID,builder.build());

    }

    public static void successNotification(Context context,String tweet,int NOTIF_ID){
        Log.d(TAG, "successNotification: called");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Tweet Sent")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(tweet)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIF_ID,builder.build());
    }

    public static void failureNotification(Context context,String reason,int NOTIF_ID){
        Log.d(TAG, "failureNotification: called");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Failure sending tweet")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(reason)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIF_ID,builder.build());
    }
}
