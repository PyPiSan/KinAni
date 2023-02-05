package com.pypisan.kinani.api;

import android.content.Context;
import android.util.Log;

import com.pypisan.kinani.model.AnimeModel;
import com.pypisan.kinani.model.AnimeRecentModel;
import com.pypisan.kinani.model.RecentlyAiredModel;
import com.pypisan.kinani.storage.AnimeManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRequest {

    public void recentFetcher(Context context) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anime.pypisan.com/v1/anime/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule animeRecent = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call = animeRecent.getAnime();

        call.enqueue(new Callback<AnimeRecentModel>() {
            @Override
            public void onResponse(Call<AnimeRecentModel> call, Response<AnimeRecentModel> response) {
                AnimeManager animeManager = new AnimeManager(context);
//                Log.d("Hey1", "Response code is : " + response.code());
                AnimeRecentModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<AnimeRecentModel.datum> data = resource.getData();
                    animeManager.open();
                    for (AnimeRecentModel.datum animes : data) {
//                        Log.d("Hey3", "Response code is : " + animes.getTitle());
                        animeManager.insertRecent(animes.getAnimeDetailLink(),
                                animes.getTitle(), animes.getImageLink());
                    }
                    animeManager.close();
                } else {
//                    Log.d("Hey2", "Response code is : " + response.code());
                }
            }
            @Override
            public void onFailure(Call<AnimeRecentModel> call, Throwable t) {
//                Log.d("Hey3", "Response code is : 400" + t.getMessage());
            }
        });
    }

//    public void scheduleFetcher(Context context){
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://anime.pypisan.com/v1/anime/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        RequestModule animeSchedule = retrofit.create(RequestModule.class);
//        Call<RecentlyAiredModel> call = animeSchedule.getAnimeSchedule();
//
//        call.enqueue(new Callback<RecentlyAiredModel>() {
//            @Override
//            public void onResponse(Call<RecentlyAiredModel> call, Response<RecentlyAiredModel> response) {
//                AnimeManager animeManager = new AnimeManager(context);
//
//                RecentlyAiredModel resource = response.body();
//                boolean status = resource.getSuccess();
//                if (status){
//                    List<RecentlyAiredModel.datum> data = resource.getData();
//                    animeManager.open();
//                    for (RecentlyAiredModel.datum animes : data) {
////                        Log.d("Hey3", "Response code is : " + animes.getSchedule());
//
//                        animeManager.insertScheduleList(animes.getJname(), animes.getTitle(),
//                                animes.getImage(), animes.getEpisode(), animes.getSchedule());
//                    }
//                    animeManager.close();
//                }else{
//                }
//            }
//            @Override
//            public void onFailure(Call<RecentlyAiredModel> call, Throwable t) {
//
//            }
//        });
//
//    }

}
