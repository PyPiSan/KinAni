package com.pypisan.kinani.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pypisan.kinani.R;
import com.pypisan.kinani.model.ContinueWatchingModel;

import java.util.ArrayList;

public class ContinueWatchingAdapter extends RecyclerView.Adapter<ContinueWatchingAdapter.ContinueWatchingViewHolder> {

    private final ArrayList<ContinueWatchingModel> continueWatchingData;
    private final Context context;
    private final SelectListener listener;

    public ContinueWatchingAdapter(ArrayList<ContinueWatchingModel> continueWatchingData, Context context,
                                   SelectListener listener) {
        this.continueWatchingData = continueWatchingData;
        this.context = context;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ContinueWatchingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.continue_watching_adapter, parent, false);
        return new ContinueWatchingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContinueWatchingAdapter.ContinueWatchingViewHolder holder,
                                 int position) {
        ImageView animeView = holder.animeView;
        CardView cardView = holder.cardView;

        String imageLink = continueWatchingData.get(position).getImage();
        String name = continueWatchingData.get(position).getTitle();
        String episodeNum = continueWatchingData.get(position).getEpisode();
        String detail = continueWatchingData.get(position).getDetail();
        String showType = continueWatchingData.get(position).getShowType();
        Integer time = continueWatchingData.get(position).getTime();

        Glide.with(context)
                .load(imageLink)
                .into(animeView);

        holder.cardView.setOnClickListener(view -> {
            listener.onItemClicked(name,detail,imageLink,showType,episodeNum,time);
        });

    }

    @Override
    public int getItemCount() {
        return continueWatchingData.size();
    }

    public class ContinueWatchingViewHolder extends RecyclerView.ViewHolder{

        ImageView animeView;
        CardView cardView;
        public ContinueWatchingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animeView = itemView.findViewById(R.id.animePicture);
            this.cardView = itemView.findViewById(R.id.card_view_continue);
        }
    }

    public interface SelectListener {
        void onItemClicked(String title, String detail, String image, String showType,String episode,
                           Integer time);
    }
}
