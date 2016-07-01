package com.example.punit.twitterclient.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.example.punit.twitterclient.R;


public class TimelineActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView page_title;
    private static final String TAG = "TimelineActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        page_title = (TextView) toolbar.findViewById(R.id.page_title);
        setSupportActionBar(toolbar);
        page_title.setText(getResources().getString(R.string.timeline_title));
        Log.d(TAG, "onCreate: called");

    }
}
