package com.example.punit.twitterclient.util;

import android.text.util.Linkify;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {


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
}
