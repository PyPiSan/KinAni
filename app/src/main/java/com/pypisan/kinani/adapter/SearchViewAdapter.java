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

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.SearchViewHolder> {

    //    Step 2 in adapter...

    private final ArrayList<AnimeModel> dataSearch;
    private final Context context;
    private final SelectListener listener;

    public SearchViewAdapter(ArrayList<AnimeModel> dataSearch, Context context, SelectListener listener) {
        this.dataSearch = dataSearch;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchViewAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_view_adapter, parent, false);
        return new SearchViewAdapter.SearchViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchViewAdapter.SearchViewHolder holder, int position) {
        ImageView animeView = holder.animeView;
        TextView animeName = holder.animeName;
        TextView releaseDate = holder.releasedYear;
        TextView releasedStatus = holder.releasedStatus;
        String animeTitle = dataSearch.get(position).getTitle();
        String animeImage = dataSearch.get(position).getImage();

        Glide.with(context)
                .load(animeImage)
                .into(animeView);

        animeName.setText(animeTitle);
        releaseDate.setText(dataSearch.get(position).getReleased());
        releasedStatus.setText(dataSearch.get(position).getReleaseStatus());

        holder.cardView.setOnClickListener(view -> {
            listener.onItemClicked(animeTitle, "", animeImage,dataSearch.get(position).getShowType());
        });
    }

    @Override
    public int getItemCount() {
        return dataSearch.size();
    }

    public interface SelectListener {
        void onItemClicked(String title,String detail, String image, String type);
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView animeView;
        TextView animeName, releasedYear, releasedStatus;
        CardView cardView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animeView = itemView.findViewById(R.id.animeImage);
            this.animeName = itemView.findViewById(R.id.animeNameSearch);
            this.cardView = itemView.findViewById(R.id.searchCard);
            this.releasedYear = itemView.findViewById(R.id.releaseYear);
            this.releasedStatus = itemView.findViewById(R.id.releaseStatus);
        }
    }
}
