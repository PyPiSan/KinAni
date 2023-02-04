package com.pypisan.kinani.api;

import com.pypisan.kinani.model.Jtitle;
import com.pypisan.kinani.model.AnimeEpisodeListModel;
import com.pypisan.kinani.model.AnimeRecentModel;
import com.pypisan.kinani.model.EpisodeVideoModel;
import com.pypisan.kinani.model.RecentlyAiredModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestModule {

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @GET("recent")
    Call<AnimeRecentModel> getAnime();

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @POST("episodes")
    Call<AnimeEpisodeListModel> getEpisodeList(@Body Jtitle body);

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @POST("watch_link")
    Call<EpisodeVideoModel> getEpisodeVideo(@Body WatchRequest body);

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @GET("trending")
    Call<AnimeRecentModel> getAnimeTrending();

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @GET("popular")
    Call<AnimeRecentModel> getAnimeRecommend();

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @GET("search/")
    Call<AnimeRecentModel> searchAnime(@Query("name") String one);

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @GET("schedule")
    Call<RecentlyAiredModel> getAnimeSchedule();

    @Headers({
            "x-api-key: e7y6acFyHGqwtkBLKHx6eA"
    })
    @GET("new/")
    Call<AnimeRecentModel> newAnime(@Query("page") String num);

}

