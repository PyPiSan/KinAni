package com.pypisan.kinani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
//     add logic here

    }

    @Override
    public int getItemCount() {return savedVideos.size();}

    public interface SelectListener {
        void onItemClicked(String title, String Episode, String showType);
    }

    public class SavedVideosViewHolder extends RecyclerView.ViewHolder{

        public SavedVideosViewHolder(@NonNull View itemView) {
            super(itemView);
//            add params
        }
    }
}
