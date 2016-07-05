package com.example.punit.twitterclient.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    private static final String TAG = "Utility";

    /**
     * creates clickable hashtags,mentions and links in supplied TextView
     * @param textView
     */
    public static void hashTagsAndLinks(TextView textView){

        Pattern atMentionPattern = Pattern.compile("@([A-Za-z0-9_]+)");
        String atMentionScheme = "http://twitter.com/";

        Pattern atHashTagPattern = Pattern.compile("#([A-Za-z0-9_]+)");
        String atHashTagScheme = "http://twitter.com/hashtag/";

        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
            //skip the first character to filter out '@'
            public String transformUrl(final Matcher match, String url) {
                return match.group(1);
            }
        };

        Linkify.addLinks(textView, Linkify.ALL);
        Linkify.addLinks(textView, atMentionPattern, atMentionScheme, null, transformFilter);
        Linkify.addLinks(textView, atHashTagPattern, atHashTagScheme, null, transformFilter);

    }

    @SuppressLint("NewApi")
    public static String getPathFromURI(Context context,Uri photo_path) {
        Log.d(TAG, "getPathFromURI: " + photo_path);
        Log.d(TAG, "getPathFromURI: " + context.getContentResolver().getType(photo_path));
        String filePath = "";

        String wholeID = DocumentsContract.getDocumentId(photo_path);

        Log.d(TAG, "getPathFromURI: " + wholeID);

        String id = wholeID.split(":")[1];
        Log.d(TAG, "getPathFromURI: " + id);

        String[] column = {MediaStore.Images.Media.DATA};

        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column,
                sel,
                new String[]{id},
                null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
}
