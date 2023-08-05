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
import android.widget.Toast;

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

    private final  int VIEW_TYPE_ITEM=0;
    private final  int VIEW_TYPE_LOADING=1;
    public RecentAdapter(Context context, ArrayList<AnimeModel> data, SelectListener listener) {
        this.context = context;
        this.dataSet = data;
        this.listener = listener;
    }

//    Step 3 in adapter...

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.recent_card_view, parent, false);
//        return new RecentViewHolder(view);

        View root = null;
        if (viewType == VIEW_TYPE_ITEM) {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_card_view, parent, false);
            return new DataViewHolder(root);
        } else {
            root = LayoutInflater.from(parent.getContext()).inflate(R.layout.loader_progress, parent, false);
            return new ProgressViewHolder(root);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewHolder holder, int position) {
        if (holder instanceof DataViewHolder) {
            ImageView animeView = holder.animeView;
            TextView animeName = holder.animeName;

            String animeTitle = dataSet.get(position).getTitle();
            String animeImage = dataSet.get(position).getImage();
            String animeDetailLink = dataSet.get(position).getAnimeDetailLink();
            String released = dataSet.get(position).getReleased();

            Glide.with(context)
                    .load(animeImage)
                    .into(animeView);

            animeName.setText(animeTitle);

            holder.cardView.setOnClickListener(view -> {
//            Toast.makeText(view.getContext(), "Card is " + animeTitle, Toast.LENGTH_SHORT).show();
                        listener.onItemClicked(animeTitle, animeDetailLink, animeImage);

                    }
            );
        }

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface SelectListener {
        void onItemClicked(String title, String detail, String image);
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

    public class  ProgressViewHolder extends RecentViewHolder{

        public ProgressViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class DataViewHolder extends RecentViewHolder{

        public DataViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (dataSet.get(position) != null)
            return VIEW_TYPE_ITEM;
        else
            return VIEW_TYPE_LOADING;
    }

    public void addNullData() {
        dataSet.add(null);
        notifyItemInserted(dataSet.size() +1);
    }
    public void removeNull(int pos) {
//        Log.d("T", "size is " +dataSet.size()+"pos is "+pos);
        dataSet.remove(pos);
        notifyItemRemoved(dataSet.size()-1);
    }

}

