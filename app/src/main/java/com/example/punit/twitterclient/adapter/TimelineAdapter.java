package com.example.punit.twitterclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.punit.twitterclient.R;
import com.example.punit.twitterclient.model.Timeline;
import com.example.punit.twitterclient.util.ClickListener;
import com.example.punit.twitterclient.util.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //Constants for diff ViewTypes in Recyclerview
    public static final int ITEM = 0;
    public static final int LOADING = 1;

    private static final String TAG = "CustomTimelineAdapter";

    private Context context;
    private ArrayList<Timeline> tweets;
    static ClickListener listener;
    private boolean isLoadingFooterAdded = false;

    /**
     * Constructor used to load initial batch of tweets
     * @param context
     * @param tweets
     */
    public TimelineAdapter(Context context, ArrayList<Timeline> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    /**
     * Creates ViewHolder for various viewTypes.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case ITEM:
                holder = createItemViewHolder(parent);
                break;
            case LOADING:
                holder = createLoadMoreHolder(parent);
                break;
            default:
                break;

        }
        return holder;
    }

    /**
     * Depending on position layouts child views
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case ITEM:
                BindItemViewHolder(holder,position);
                break;
            case LOADING:
                BindLoadMoreHolder(holder);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    /**
     * Returns ViewType depending on item position and loading process
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return (position == tweets.size() - 1 && isLoadingFooterAdded) ? LOADING : ITEM;
    }

    public Timeline getItem(int position){
        return tweets.get(position);
    }

    private void add(Timeline tweet){
        tweets.add(tweet);
    }

    /**
     * Called from Timeline Activity when new set of Tweets are obtained from API and needs to be added.
     * @param tweets
     */
    public void addAll(ArrayList<Timeline> tweets){
        for(Timeline tweet:tweets){
            add(tweet);
        }
    }

    /**
     * Starts ProgressBar loading indicator indicating fetching of next set of Tweets.
     */
    public void addLoading(){
        isLoadingFooterAdded = true;
        add(new Timeline());
    }


    /**
     * Once tweets are received removes the progress bar row added earlier during load process
     */
    public void removeLoading(){
        isLoadingFooterAdded = false;

        int position = tweets.size() - 1;
        Timeline tweet = getItem(position);
        if(tweet!=null) {
            tweets.remove(tweet);
            notifyItemRemoved(position);
        }
    }

    /**
     * Tweet Item row ViewHolder
     * @param parent
     * @return
     */
    private RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_row_view,parent,false);
        return new ItemViewHolder(v);
    }

    /**
     * Progress Bar row ViewHolder
     * @param parent
     * @return
     */
    private RecyclerView.ViewHolder createLoadMoreHolder(ViewGroup parent){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_view,parent,false);
        return new LoadMoreHolder(v);
    }

    /**
     * Tweet Item row BindViewHolder.Loads data into views using Viewholder created earlier.
     * @param holder
     * @param position
     */
    private void BindItemViewHolder(RecyclerView.ViewHolder holder,int position){
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.username.setText(tweets.get(position).user.name);
        itemViewHolder.user_tweet.setText(tweets.get(position).text);

        //Make #Hashtag,@Mentions and Links clickable in tweet textview
        Utility.hashTagsAndLinks(itemViewHolder.user_tweet);
        itemViewHolder.twitter_name.setText("@" +tweets.get(position).user.screenName);

        Picasso.with(context)
                .load(tweets.get(position).user.profileImageUrl)
                .placeholder(R.drawable.profile_image_placeholder)
                .into(itemViewHolder.profile_image);

    }

    /**
     * Progress Bar row BindViewHolder. Loads data into views using ViewHolder created earlier.
     * @param holder
     */
    private void BindLoadMoreHolder(RecyclerView.ViewHolder holder){
        LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
        loadMoreHolder.progressBar.setIndeterminate(true);
    }

    /**
     * Tweet row viewHolder implementation
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.user_profile_img_timeline_row) ImageView profile_image;
        @BindView(R.id.username_timeline_row) TextView username;
        @BindView(R.id.tweet_timeline_row) TextView user_tweet;
        @BindView(R.id.twitter_name_timeline_row) TextView twitter_name;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            //Setup Click listeners to open Detail Tweet Activity
            itemView.setOnClickListener(this);
            user_tweet.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.itemClicked(v,getAdapterPosition());
        }
    }

    /**
     * Progress Bar Viewholder implementation
     */
    public static class LoadMoreHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        public LoadMoreHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }




    //ClickListener Initialization
    public void setClickListener(ClickListener listener){
        this.listener = listener;
    }
}

