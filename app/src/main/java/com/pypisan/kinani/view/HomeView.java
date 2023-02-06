package com.pypisan.kinani.view;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.pypisan.kinani.R;
import com.pypisan.kinani.adapter.HomeViewAdapter;
import com.pypisan.kinani.adapter.RecentlyAiredAdapter;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.model.AnimeModel;
import com.pypisan.kinani.model.AnimeRecentModel;
import com.pypisan.kinani.model.RecentlyAiredModel;
import com.pypisan.kinani.model.ScheduleModel;
import com.pypisan.kinani.storage.AnimeManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeView extends Fragment implements HomeViewAdapter.SelectListener {

    // Add RecyclerView member
    private ArrayList<AnimeModel> animeRecentNum, animeTrendingList, animeTrendingListInc,
            animeRecommendList, animeRecommendListInc;

    private RecyclerView recyclerView_recent, recyclerView_trending, recyclerView_recommend,
            recyclerView_schedule;

    private RecyclerView.Adapter adapter, adapterTrending, scheduleAdapter, adapterRecommend;
    private AnimeManager animeManager;

    private ArrayList<ScheduleModel> animeScheduleList, animeScheduleListInc;
    private ShimmerFrameLayout containerTrending, containerSchedule, containerRecommend,
            containerRecent;
    private TextView recentTextHeader;

    public HomeView() {
        // Required empty public constructor
    }

    public static HomeView newInstance() {
        return new HomeView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        animeTrendingListInc = new ArrayList<>();
        animeScheduleListInc = new ArrayList<>();
        animeRecommendListInc = new ArrayList<>();

        containerRecent = view.findViewById(R.id.shimmer_view_container_recent);
        containerTrending = view.findViewById(R.id.shimmer_view_container);
        containerSchedule = view.findViewById(R.id.shimmer_view_container_schedule);
        containerRecommend = view.findViewById(R.id.shimmer_view_container_recommend);

        recentTextHeader = view.findViewById(R.id.recents);

//        Starting Shimmer Effect
        containerRecent.startShimmer();
        containerTrending.startShimmer();
        containerSchedule.startShimmer();
        containerRecommend.startShimmer();

//        Fetching Data
        scheduleFetcher();
        recentlyWatchedList();
        getTrendingList();
        recommendFetcher();

//      1st initialization recycler recent
        recyclerView_recent = view.findViewById(R.id.home_recycler_view_recent);
        recyclerView_recent.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView_recent.setHasFixedSize(true);

        //        Setting Data
        adapter = new HomeViewAdapter(animeRecentNum, getContext(), this);
        containerRecent.stopShimmer();
        containerRecent.setVisibility(View.GONE);
        recyclerView_recent.setVisibility(View.VISIBLE);
        recyclerView_recent.setItemAnimator(new DefaultItemAnimator());
        recyclerView_recent.setAdapter(adapter);


//        2nd recycler view trending
        recyclerView_trending = view.findViewById(R.id.home_recycler_view_trending);
        recyclerView_trending.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView_trending.setHasFixedSize(true);

//        Setting Data
        adapterTrending = new HomeViewAdapter(animeTrendingListInc, getContext(), this);

//        3rd recycler view recommends
        recyclerView_recommend = view.findViewById(R.id.home_recycler_view_recommends);
        recyclerView_recommend.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView_recommend.setHasFixedSize(true);

//        Setting Data
        adapterRecommend = new HomeViewAdapter(animeRecommendListInc, getContext(), this);


//        4th recycler view aired at
        recyclerView_schedule = view.findViewById(R.id.home_recycler_view_recentlyAired);
        recyclerView_schedule.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_schedule.setHasFixedSize(true);

//        Setting Data
        scheduleAdapter = new RecentlyAiredAdapter(animeScheduleListInc, getContext(), this::onItemClicked);

    }
//       Fetching Liked list from DB

    private void recentlyWatchedList() {
        animeRecentNum = new ArrayList<>();
        animeManager = new AnimeManager(getContext());
        animeManager.open();
        Cursor cursor = animeManager.readAllDataRecent();
        if (cursor.getCount() == 0) {
//            Toast.makeText(getContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
        } else {
            recentTextHeader.setVisibility(View.VISIBLE);
            AnimeModel model;
//            Log.d("H1", "anime list is " + cursor.getCount());
            int i = 1;
            while (cursor.moveToNext()) {
                model = new AnimeModel(cursor.getString(3), cursor.getString(1), cursor.getString(2), null);
//                Log.d("H3", "cursor at " + i + cursor.getString(1));
                animeRecentNum.add(model);
//                Log.d("H4", "anime list is " + animeNum.size());
                i++;
                if (i == 10) {
                    break;
                }
            }
            animeManager.close();
        }
    }


    @Override
    public void onItemClicked(String title, String detail, String image) {
        animeManager = new AnimeManager(getContext());
        animeManager.open();
        animeManager.insertRecent(detail, title, image);
        animeManager.close();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        Fragment fragment = SummaryView.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentView, fragment, "summary_view");
        transaction.addToBackStack(null);
        transaction.commit();

    }

//    public int getSkeletonRowCount(Context context) {
//        int pxHeight = getDeviceHeight(context);
//        int skeletonRowHeight = (int) getResources()
//                .getDimension(R.dimen.image_height); //converts to pixel
//        return (int) Math.ceil(pxHeight / skeletonRowHeight);
//    }
//    public int getDeviceHeight(Context context){
//        Resources resources = context.getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        return metrics.heightPixels;
//    }


    //  fetching data
    private void getTrendingList() {
        animeTrendingList = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anime.pypisan.com/v1/anime/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule animeTrend = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call = animeTrend.getAnimeTrending();

        call.enqueue(new Callback<AnimeRecentModel>() {
            @Override
            public void onResponse(Call<AnimeRecentModel> call, Response<AnimeRecentModel> response) {
//                Log.d("Hey1", "Response code is : " + response.code());
                AnimeRecentModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<AnimeRecentModel.datum> data = resource.getData();
                    AnimeModel model = new AnimeModel();
                    for (AnimeRecentModel.datum animes : data) {
//                        Log.d("Hey3", "Response code is : " + response.body() +  i);
                        model = new AnimeModel(animes.getImageLink(),
                                animes.getAnimeDetailLink(), animes.getTitle(), animes.getReleased());
                        animeTrendingList.add(model);
//                        Log.d("hello1", "anime list is " + i);
//                        i +=1;
                    }
                    onTrendingDataLoad(0);
                    containerTrending.stopShimmer();
                    containerTrending.setVisibility(View.GONE);
                    recyclerView_trending.setVisibility(View.VISIBLE);
                    recyclerView_trending.setItemAnimator(new DefaultItemAnimator());
                    recyclerView_trending.setAdapter(adapterTrending);
                }
            }

            @Override
            public void onFailure(Call<AnimeRecentModel> call, Throwable t) {
//                Log.d("Hey3", "Response code is : 400" + t.getMessage());
            }
        });
    }

    private void onTrendingDataLoad(int offset) {
        if (animeTrendingListInc != null) {
            animeTrendingListInc.clear();
        }
        if (animeTrendingList.size() <= 10) {
            for (int i = offset; i < animeTrendingList.size(); i++) {
                animeTrendingListInc.add(animeTrendingList.get(i));
            }
            adapterTrending.notifyDataSetChanged();
        } else {
            for (int i = offset; i < 10; i++) {
                animeTrendingListInc.add(animeTrendingList.get(i));
            }
            adapterTrending.notifyDataSetChanged();
        }
    }

    //  fetching data
    private void recommendFetcher() {
        animeRecommendList = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anime.pypisan.com/v1/anime/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule animeRecommend = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call = animeRecommend.getAnimeRecommend();

        call.enqueue(new Callback<AnimeRecentModel>() {
            @Override
            public void onResponse(Call<AnimeRecentModel> call, Response<AnimeRecentModel> response) {
//                Log.d("Hey1", "Response code is : " + response.code());
                AnimeRecentModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<AnimeRecentModel.datum> data = resource.getData();
                    AnimeModel modelRecommend = new AnimeModel();
                    for (AnimeRecentModel.datum animes : data) {
//                        Log.d("Hey3", "Response code is : " + response.body() +  i);
                        modelRecommend = new AnimeModel(animes.getImageLink(),
                                animes.getAnimeDetailLink(), animes.getTitle(), animes.getReleased());
                        animeRecommendList.add(modelRecommend);
//                        Log.d("hello1", "anime list is " + i);
//                        i +=1;
                    }
                    onRecommendDataLoad(0);
                    containerRecommend.stopShimmer();
                    containerRecommend.setVisibility(View.GONE);
                    recyclerView_recommend.setVisibility(View.VISIBLE);
                    recyclerView_recommend.setItemAnimator(new DefaultItemAnimator());
                    recyclerView_recommend.setAdapter(adapterRecommend);
                }
            }

            @Override
            public void onFailure(Call<AnimeRecentModel> call, Throwable t) {
//                Log.d("Hey3", "Response code is : 400" + t.getMessage());
            }
        });
    }

    private void onRecommendDataLoad(int offset) {
        if (animeRecommendListInc != null) {
            animeRecommendListInc.clear();
        }
        if (animeRecommendList.size() <= 10) {
            for (int i = offset; i < animeRecommendList.size(); i++) {
                animeRecommendListInc.add(animeRecommendList.get(i));
            }
            adapterRecommend.notifyItemInserted(animeRecommendList.size());
        } else {
            for (int i = offset; i < 10; i++) {
                animeRecommendListInc.add(animeRecommendList.get(i));
            }
            adapterRecommend.notifyItemInserted(9);
        }
    }

    private void scheduleFetcher() {
        animeScheduleList = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anime.pypisan.com/v1/anime/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule animeSchedule = retrofit.create(RequestModule.class);
        Call<RecentlyAiredModel> call = animeSchedule.getAnimeSchedule();

        call.enqueue(new Callback<RecentlyAiredModel>() {
            @Override
            public void onResponse(Call<RecentlyAiredModel> call, Response<RecentlyAiredModel> response) {

                RecentlyAiredModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<RecentlyAiredModel.datum> data = resource.getData();
                    ScheduleModel scheduleModel = new ScheduleModel();
                    for (RecentlyAiredModel.datum animes : data) {
//                        Log.d("Hey3", "Response code is : " + animes.getSchedule());

                        scheduleModel = new ScheduleModel(animes.getTitle(),
                                animes.getImage(), animes.getEpisode(), animes.getSchedule());
                        animeScheduleList.add(scheduleModel);
                    }
                }
//                Log.d("Home", "List size is : " + animeScheduleList.size());
                onScheduleDataLoad(0);
                containerSchedule.stopShimmer();
                containerSchedule.setVisibility(View.GONE);
                recyclerView_schedule.setVisibility(View.VISIBLE);
                recyclerView_schedule.setItemAnimator(new DefaultItemAnimator());
                recyclerView_schedule.setAdapter(scheduleAdapter);
            }

            @Override
            public void onFailure(Call<RecentlyAiredModel> call, Throwable t) {

            }
        });

    }

    private void onScheduleDataLoad(int offset) {
        if (animeScheduleListInc != null) {
            animeScheduleListInc.clear();
        }
        if (animeScheduleList.size() <= 10) {
            for (int i = offset; i < animeScheduleList.size(); i++) {
                animeScheduleListInc.add(animeScheduleList.get(i));
            }
            scheduleAdapter.notifyItemInserted(animeScheduleList.size());
        } else {
            for (int i = offset; i < 10; i++) {
                animeScheduleListInc.add(animeScheduleList.get(i));
            }
            scheduleAdapter.notifyItemInserted(9);
        }
    }

}