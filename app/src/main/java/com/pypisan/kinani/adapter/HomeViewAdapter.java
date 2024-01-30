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
import com.pypisan.kinani.model.AnimeModel;

import java.util.ArrayList;

public class HomeViewAdapter extends RecyclerView.Adapter<HomeViewAdapter.HomeViewHolder> {

    //    Step 2 in adapter...

    private final ArrayList<AnimeModel> dataSet;
    private final Context context;
    private final SelectListener listener;

    public HomeViewAdapter(ArrayList<AnimeModel> dataSet, Context context, SelectListener listener) {
        this.dataSet = dataSet;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_view_adapter, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        ImageView animeView = holder.animeView;
        TextView animeName = holder.animeName;
        if (dataSet.size() == 0) {
            Log.d("Nothing", "Nothing to do");
        }
        String animeTitle = dataSet.get(position).getTitle();
        String animeImage = dataSet.get(position).getImage();
        String animeDetail = dataSet.get(position).getAnimeDetailLink();
        String showType = dataSet.get(position).getShowType();

        Glide.with(context)
                .load(animeImage)
                .into(animeView);

        animeName.setText(animeTitle);

        holder.cardView.setOnClickListener(view -> {
//            Toast.makeText(view.getContext(), "Card is " + animeJtitle, Toast.LENGTH_SHORT).show();
            listener.onItemClicked(animeTitle,animeDetail, animeImage,showType);
        });
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class HomeViewHolder extends RecyclerView.ViewHolder{
        ImageView animeView;
        TextView animeName;
        CardView cardView;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animeView = itemView.findViewById(R.id.animePhoto);
            this.animeName = itemView.findViewById(R.id.animeInfo);
            this.cardView = itemView.findViewById(R.id.card_view_home);
        }
    }
    public interface SelectListener{
        void onItemClicked(String title, String detail, String image, String showType);
    }
}
