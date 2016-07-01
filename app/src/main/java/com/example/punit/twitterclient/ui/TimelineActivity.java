package com.example.punit.twitterclient.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.punit.twitterclient.R;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = "TimelineActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Log.d(TAG, "onCreate: called");

    }
}
