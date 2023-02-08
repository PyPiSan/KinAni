package com.pypisan.kinani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pypisan.kinani.R;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {


    private final int listEpisode;
    private final Context context;
    private final SelectListener listener;

    public EpisodeAdapter(Context context, int listEpisode, SelectListener listener) {
        this.listEpisode = listEpisode;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.episode_adapter, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        TextView epValue = holder.epVal;
        CardView epCard = holder.episode;
        epValue.setText(String.valueOf(position+1));
        epCard.setOnClickListener(view -> {
            listener.onEpisodeClicked(position+1);
        });

    }

    @Override
    public int getItemCount() {
        return listEpisode;
    }

    public interface SelectListener {
        void onEpisodeClicked(int episode_num);
    }

    public class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView epVal;
        CardView episode;

        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.episode = itemView.findViewById(R.id.ep);
            this.epVal = itemView.findViewById(R.id.epVal);

        }
    }
}
