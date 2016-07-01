package com.example.punit.twitterclient.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.dagger.DaggerInjector;
import com.example.punit.twitterclient.util.Constants;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.twitter_login_btn) TwitterLoginButton loginButton;
    private static final String TAG = "MainActivity";
    @Inject SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerInjector.getAppComponent().inject(this);
        if(preferences.getBoolean(Constants.USER_LOGGED_IN,false)){
            Intent intent = new Intent(MainActivity.this,TimelineActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                long user_id = session.getUserId();
                String user_name = session.getUserName();
                Log.d(TAG, "success: " + String.valueOf(user_id) + " " + user_name);

                editor = preferences.edit();
                editor.putBoolean(Constants.USER_LOGGED_IN,true);
                editor.putLong(Constants.USER_ID,user_id);
                editor.putString(Constants.USER_NAME,user_name);
                editor.apply();


                Intent i = new Intent(MainActivity.this,TimelineActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this,R.string.login_failure_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode,resultCode,data);
    }
}
