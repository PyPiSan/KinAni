package com.pypisan.kinani.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pypisan.kinani.R;
import com.pypisan.kinani.adapter.SavedVideosAdapter;
import com.pypisan.kinani.model.SavedVideosModel;
import com.pypisan.kinani.storage.Constant;
import java.io.File;
import java.util.ArrayList;

public class SavedVideos extends Fragment implements SavedVideosAdapter.SelectListener {


    private ArrayList<SavedVideosModel> savedList;
    private RecyclerView recyclerView_saved_video;
    private RecyclerView.Adapter adapterSavedVideos;

    public SavedVideos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.saved_videos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Files", "Path: " + Constant.storageLocation);
        File directory = new File(Constant.storageLocation);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        SavedVideosModel savedVideosModel;
        savedList = new ArrayList<>();
        for (File file : files) {
            Log.d("Files", "FileName:" + file.getName());
            savedVideosModel = new SavedVideosModel("Hello World","10","anime");
            savedList.add(savedVideosModel);
        }
        adapterSavedVideos.notifyItemInserted(savedList.size());



//      recycler view saved videos
        recyclerView_saved_video = view.findViewById(R.id.recycler_view_saved_videos);
        recyclerView_saved_video.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_saved_video.setHasFixedSize(false);

//      Setting Data
        adapterSavedVideos = new SavedVideosAdapter(savedList, getContext(), this::onItemClicked);

        recyclerView_saved_video.setItemAnimator(new DefaultItemAnimator());
        recyclerView_saved_video.setAdapter(adapterSavedVideos);

    }




    @Override
    public void onItemClicked(String title, String Episode, String type) {

    }

}