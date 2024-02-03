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
        TextView animeName = holder.name;
        TextView episodeView = holder.episodeNum;
        TextView timeView = holder.timeView;

        String imageLink = continueWatchingData.get(position).getImage();
        String name = continueWatchingData.get(position).getTitle();
        String episodeNum = continueWatchingData.get(position).getEpisode();
        String detail = continueWatchingData.get(position).getDetail();
        String showType = continueWatchingData.get(position).getShowType();
        Integer time = continueWatchingData.get(position).getTime();

        Glide.with(context)
                .load(imageLink)
                .into(animeView);
        animeName.setText(name);
        episodeView.setText(String.format("Episode: %s", episodeNum));
        timeView.setText(formatSeconds(time/1000));
        holder.cardView.setOnClickListener(view -> {
            listener.onItemClicked(name,detail,imageLink,showType,episodeNum,String.valueOf(time));
        });

    }

    @Override
    public int getItemCount() {
        return continueWatchingData.size();
    }

    public class ContinueWatchingViewHolder extends RecyclerView.ViewHolder{

        ImageView animeView;
        CardView cardView;
        TextView name,episodeNum,timeView;
        public ContinueWatchingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animeView = itemView.findViewById(R.id.animePicture);
            this.cardView = itemView.findViewById(R.id.card_view_continue);
            this.name = itemView.findViewById(R.id.animeName);
            this.episodeNum = itemView.findViewById(R.id.episodeNum);
            this.timeView = itemView.findViewById(R.id.time);
        }
    }

    public interface SelectListener {
        void onItemClicked(String title, String detail, String image, String showType,String episode,
                           String time);
    }

    public static String formatSeconds(int timeInSeconds)
    {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds ;

        return formattedTime;
    }
}
