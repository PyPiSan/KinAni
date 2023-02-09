package com.pypisan.kinani.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pypisan.kinani.R;
import com.pypisan.kinani.adapter.SearchViewAdapter;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.model.AnimeModel;
import com.pypisan.kinani.model.AnimeRecentModel;
import com.pypisan.kinani.utils.SearchableActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchListView extends Fragment implements SearchViewAdapter.SelectListener {

    // Add RecyclerView member
    private ArrayList<AnimeModel> animeSearchList;
    private RecyclerView.Adapter adapter;

    public SearchListView() {
        // Required empty public constructor
    }

    public static SearchListView newInstance() {
        return new SearchListView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_list_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String searchString = getArguments().getString("searchString");
        insertDataToCard(searchString);

        //      initialization recycler

        RecyclerView recyclerView = view.findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        //        Setting Data
//        Log.d("hell", "anime list is " + animeSearchList.size());
        adapter = new SearchViewAdapter(animeSearchList, getContext(), this::onItemClicked);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    //        Insert data to card
    private void insertDataToCard(String searchString) {
        // Add the cards data and display them
//        fetching data
        animeSearchList = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anime.pypisan.com/v1/anime/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestModule animeRecent = retrofit.create(RequestModule.class);
//        Log.d("Hi", "search code is : " + searchString);
        Call<AnimeRecentModel> call = animeRecent.searchAnime(searchString);

        call.enqueue(new Callback<AnimeRecentModel>() {
            @Override
            public void onResponse(Call<AnimeRecentModel> call, Response<AnimeRecentModel> response) {
//                Log.d("Hell", "Response code is : " + response.code());
                AnimeRecentModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<AnimeRecentModel.datum> data = resource.getData();
                    AnimeModel model;
//                    int i = 0;
                    for (AnimeRecentModel.datum animes : data) {
//                        Log.d("Hey3", "Response code is : " + response.body() +  i);
                        model = new AnimeModel(animes.getImageLink(), animes.getAnimeDetailLink(),
                                animes.getTitle(), animes.getReleased());
                        animeSearchList.add(model);
//                        Log.d("hello1", "anime list is " + i);
//                        i +=1;
//                        i +=1;
                        adapter.notifyDataSetChanged();

                    }
                } else {
//                    Toast.makeText(this, "Response not found", Toast.LENGTH_SHORT).show();
//                    Log.d("Hey2", "Response code is : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AnimeRecentModel> call, Throwable t) {
//                Log.d("Hey3", "Response code is : 400" + t.getMessage());
            }
        });
    }

    @Override
    public void onItemClicked(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        Fragment fragment = SummaryView.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.searchFragmentView, fragment, "summary_view");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}