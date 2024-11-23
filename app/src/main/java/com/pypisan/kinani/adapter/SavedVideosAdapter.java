package com.pypisan.kinani.adapter;

import android.content.Context;
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
import com.pypisan.kinani.model.SavedVideosModel;

import java.util.ArrayList;

public class SavedVideosAdapter extends RecyclerView.Adapter<SavedVideosAdapter.SavedVideosViewHolder> {

    private final ArrayList<SavedVideosModel> savedVideos;

    private final Context context;
    private final SelectListener listener;
    public SavedVideosAdapter(ArrayList<SavedVideosModel> savedVideos, Context context, SelectListener listener){
        this.savedVideos = savedVideos;
        this.context = context;
        this.listener = listener;

    }
    @NonNull
    @Override
    public SavedVideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_videos_adapter, parent, false);
        return new SavedVideosAdapter.SavedVideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedVideosViewHolder holder, int position) {
        ImageView picture = holder.picture;
        TextView videoName = holder.videoName;
        TextView episode = holder.episode;

//        String image = savedVideos.get(position).getImage();
        String name = savedVideos.get(position).getTitle();
        String episodeNum = savedVideos.get(position).getEpisode();
        String showType = savedVideos.get(position).getShowType();

        Glide.with(context)
                .load("https://gogocdn.net/cover/fairy-tail-100-years-quest.png")
                .into(picture);
        videoName.setText(name);
        episode.setText(String.format("Episode: "+episodeNum));
        holder.cardView.setOnClickListener(view -> {
            listener.onItemClicked(name, episodeNum,showType);
        });

    }

    @Override
    public int getItemCount() {return savedVideos.size();}

    public interface SelectListener {
        void onItemClicked(String title, String Episode, String showType);
    }

    public class SavedVideosViewHolder extends RecyclerView.ViewHolder{

        ImageView picture;
        TextView videoName;
        TextView episode;
        CardView cardView;
        public SavedVideosViewHolder(@NonNull View itemView) {
            super(itemView);
            this.picture = itemView.findViewById(R.id.Picture);
            this.videoName = itemView.findViewById(R.id.video_name);
            this.episode = itemView.findViewById(R.id.video_episode);
            this.cardView = itemView.findViewById(R.id.videosCard);
        }
    }
}
