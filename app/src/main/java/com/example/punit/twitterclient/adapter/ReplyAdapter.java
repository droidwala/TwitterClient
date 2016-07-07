package com.example.punit.twitterclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.punit.twitterclient.R;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.RVHolder> {


    ArrayList<Tweet> tweets;
    Context context;
    public ReplyAdapter(Context context, ArrayList<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }


    @Override
    public RVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.timeline_row_view,parent,false);
        return new RVHolder(v);
    }

    @Override
    public void onBindViewHolder(RVHolder holder, int position) {
        holder.username.setText(tweets.get(position).user.name);
        holder.twitter_name.setText("@" + tweets.get(position).user.screenName);
        holder.user_tweet.setText(tweets.get(position).text);

        Picasso.with(context)
                .load(tweets.get(position).user.profileImageUrl)
                .placeholder(R.drawable.profile_image_placeholder)
                .into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class RVHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.user_profile_img_timeline_row) ImageView profile_image;
        @BindView(R.id.username_timeline_row) TextView username;
        @BindView(R.id.tweet_timeline_row) TextView user_tweet;
        @BindView(R.id.twitter_name_timeline_row) TextView twitter_name;

        public RVHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
