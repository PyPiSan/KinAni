package com.pypisan.kinani.api;

import com.pypisan.kinani.model.AnimeEpisodeListModel;
import com.pypisan.kinani.model.AnimeRecentModel;
import com.pypisan.kinani.model.EpisodeVideoModel;
import com.pypisan.kinani.model.RecentlyAiredModel;
import com.pypisan.kinani.model.SignUpModel;
import com.pypisan.kinani.model.TriviaModel;
import com.pypisan.kinani.model.UserModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestModule {

    @POST("episodes")
    Call<AnimeEpisodeListModel> getEpisodeList(@Header("x-api-key") String apikey, @Body Title body);


    @POST("watch_link")
    Call<EpisodeVideoModel> getEpisodeVideo(@Header("x-api-key") String apikey, @Body WatchRequest body);


    @GET("trending/")
    Call<AnimeRecentModel> getTrending(@Header("x-api-key") String apikey, @Query("page") String num);


    @GET("recommendation/")
    Call<AnimeRecentModel> getRecommend(@Header("x-api-key") String apikey, @Query("page") String num);

    @GET("search/")
    Call<AnimeRecentModel> searchAnime(@Header("x-api-key") String apikey, @Query("name") String one);


    @GET("schedule/")
    Call<RecentlyAiredModel> getAnimeSchedule(@Header("x-api-key") String apikey, @Query("page") String num);


    @GET("new/")
    Call<AnimeRecentModel> newAnime(@Header("x-api-key") String apikey, @Query("page") String num);

    @GET("movies/")
    Call<AnimeRecentModel> getMovies(@Header("x-api-key") String apikey, @Query("page") String num);

    @GET("kshow/")
    Call<AnimeRecentModel> getKShows(@Header("x-api-key") String apikey, @Query("page") String num);


    @POST("users")
    Call<UserModel> getUser(@Body UserInit body);

    @POST("login")
    Call<UserModel> getLogin(@Body UserRequest body);

    @POST("signup")
    Call<SignUpModel> signUp(@Header("x-api-key") String apikey, @Body SignUpRequest body);

    @GET("about")
    Call<UserModel> getAppAbout(@Header("x-api-key") String apikey);

    @POST("users/update")
    Call<UserModel> updateUser(@Header("x-api-key") String apikey, @Body UserUpdate body);

    @POST("report")
    Call<UserModel> reportIssue(@Header("x-api-key") String apikey, @Body ReportIssue body);

    @GET("trivia")
    Call<TriviaModel> getTrivia(@Header("x-api-key") String apikey);

}

