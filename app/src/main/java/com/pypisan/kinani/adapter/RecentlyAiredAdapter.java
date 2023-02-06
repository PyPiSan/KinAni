package com.pypisan.kinani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pypisan.kinani.R;
import com.pypisan.kinani.model.ScheduleModel;

import java.util.ArrayList;

public class RecentlyAiredAdapter extends RecyclerView.Adapter<RecentlyAiredAdapter.RecentlyAiredViewHolder> {

    //    Step 2 in adapter...
    private final ArrayList<ScheduleModel> scheduleData;
    private final Context context;
    private final SelectListener listener;

    public RecentlyAiredAdapter(ArrayList<ScheduleModel> scheduleData, Context context, SelectListener listener) {
        this.scheduleData = scheduleData;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecentlyAiredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recently_aired_adapter, parent, false);
        return new RecentlyAiredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyAiredViewHolder holder, int position) {
        ImageView animeView = holder.animeView;
        TextView animeName = holder.animeName;
        TextView episode = holder.episode;
        TextView aireAt = holder.airedAt;
        CardView cardView = holder.cardView;

        if (scheduleData.size() == 0) {
//            Do Nothing
        }
        String image = scheduleData.get(position).getImage();
        String name = scheduleData.get(position).getTitle();
        String episodeNum = scheduleData.get(position).getEpisode();
        String schedule = scheduleData.get(position).getSchedule();

        Glide.with(context)
                .load(image)
                .into(animeView);
        animeName.setText(name);
        episode.setText(String.format("Episode: %s", episodeNum));
        aireAt.setText(schedule);

        holder.cardView.setOnClickListener(view -> {
//            Toast.makeText(view.getContext(), "Card is " + name + position, Toast.LENGTH_SHORT).show();
            listener.onItemClicked(name, "", image);
        });
    }

    @Override
    public int getItemCount() {
        return scheduleData.size();
    }

    public interface SelectListener {
        void onItemClicked(String title, String detail, String image);
    }

    public class RecentlyAiredViewHolder extends RecyclerView.ViewHolder {

        ImageView animeView;
        TextView animeName;
        TextView episode;
        TextView airedAt;
        CardView cardView;

        public RecentlyAiredViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animeView = itemView.findViewById(R.id.animePhoto);
            this.animeName = itemView.findViewById(R.id.name);
            this.episode = itemView.findViewById(R.id.episode);
            this.airedAt = itemView.findViewById(R.id.airedAt);
            this.cardView = itemView.findViewById(R.id.scheduleCard);
        }
    }
}
