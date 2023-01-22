package com.pypisan.kinani.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.pypisan.kinani.model.AnimeModel;

import java.util.ArrayList;

//   Step 1 create class extends recyclerView

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {


//    Step 2 in adapter...

    private final ArrayList<AnimeModel> dataSet;
    private final Context context;
    private final SelectListener listener;

    public RecentAdapter(Context context, ArrayList<AnimeModel> data, SelectListener listener) {
        this.context = context;
        this.dataSet = data;
        this.listener = listener;
    }

//    Step 3 in adapter...

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_card_view, parent, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        ImageView animeView = holder.animeView;
        TextView animeName = holder.animeName;

        String animeTitle = dataSet.get(position).getTitle();
        String animeImage = dataSet.get(position).getImage();
        String animeJtitle = dataSet.get(position).getJtitle();

        Glide.with(context)
                .load(animeImage)
                .into(animeView);

//        animeView.setImageResource(0);
//        @SuppressLint("UseCompatLoadingForDrawables") Drawable draw = context.getDrawable(R.drawable.images);
//        animeView.setImageDrawable(draw);
        animeName.setText(animeTitle);

        holder.cardView.setOnClickListener(view -> {
//            Toast.makeText(view.getContext(), "Card is " + animeJtitle, Toast.LENGTH_SHORT).show();
            listener.onItemClicked(animeJtitle);
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface SelectListener {
        void onItemClicked(String jTitle);
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder {

        ImageView animeView;
        TextView animeName;
        CardView cardView;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animeView = itemView.findViewById(R.id.animeView);
            this.animeName = itemView.findViewById(R.id.animeName);
            this.cardView = itemView.findViewById(R.id.card_view);

        }
    }

}

