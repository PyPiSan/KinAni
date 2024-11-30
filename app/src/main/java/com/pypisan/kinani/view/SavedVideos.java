package com.pypisan.kinani.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
        ImageButton backButton = view.findViewById(R.id.video_back_button);
        ImageButton refreshButton = view.findViewById(R.id.video_refresh_button);


        backButton.setOnClickListener(v -> openUserPage());

        File downloadsDir = getContext().getExternalFilesDir(Constant.storageLocation);
        if (downloadsDir != null && downloadsDir.exists()) {
            File[] files = downloadsDir.listFiles();
            SavedVideosModel savedVideosModel;
            savedList = new ArrayList<>();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    Log.d("FileList", "File: " + file.getName() + ", Path: " + file.getAbsolutePath());
                    String [] data = Constant.getName(file.getName());
                    if (data.length==3){
                        savedVideosModel = new SavedVideosModel(data[0],data[1],data[2], file.getAbsolutePath());
                        savedList.add(savedVideosModel);
                    }
                }
            } else {
                Log.d("FileList", "No files found in the directory.");
            }
        } else {
            Log.d("FileList", "Downloads directory does not exist.");
        }


//      recycler view saved videos
        recyclerView_saved_video = view.findViewById(R.id.recycler_view_saved_videos);
        recyclerView_saved_video.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_saved_video.setHasFixedSize(false);

//      Setting Data
        adapterSavedVideos = new SavedVideosAdapter(savedList, getContext(), this::onItemClicked);
        adapterSavedVideos.notifyItemInserted(savedList.size());

        recyclerView_saved_video.setItemAnimator(new DefaultItemAnimator());
        recyclerView_saved_video.setAdapter(adapterSavedVideos);

    }




    @Override
    public void onItemClicked(String title, String Episode, String type) {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    private void openUserPage(){
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

}