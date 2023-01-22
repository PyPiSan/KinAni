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

    private final ArrayList<AnimeModel> dataSet;
    private final Context context;
    private final SelectListener listener;
    private String animeJtitle;

    public SearchViewAdapter(ArrayList<AnimeModel> dataSet, Context context, SelectListener listener) {
        this.dataSet = dataSet;
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
        if (dataSet.size() == 0) {
            Log.d("Nothing", "Nothing to do");
        }
        String animeTitle = dataSet.get(position).getTitle();
        String animeImage = dataSet.get(position).getImage();
        animeJtitle = dataSet.get(position).getJtitle();

        Glide.with(context)
                .load(animeImage)
                .into(animeView);

        animeName.setText(animeTitle);

        holder.animeView.setOnClickListener(view -> {
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

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView animeView;
        TextView animeName;
        CardView cardView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            this.animeView = itemView.findViewById(R.id.animeImage);
            this.animeName = itemView.findViewById(R.id.animeNameSearh);
        }
    }
}
