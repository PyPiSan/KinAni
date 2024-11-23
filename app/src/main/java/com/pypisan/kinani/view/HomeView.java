package com.pypisan.kinani.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.pypisan.kinani.R;
import com.pypisan.kinani.adapter.ContinueWatchingAdapter;
import com.pypisan.kinani.adapter.HomeViewAdapter;
import com.pypisan.kinani.adapter.RecentlyAiredAdapter;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.model.AnimeModel;
import com.pypisan.kinani.model.AnimeRecentModel;
import com.pypisan.kinani.model.ContinueWatchingModel;
import com.pypisan.kinani.model.RecentlyAiredModel;
import com.pypisan.kinani.model.ScheduleModel;
import com.pypisan.kinani.model.TriviaModel;
import com.pypisan.kinani.play.VideoPlayer;
import com.pypisan.kinani.storage.AnimeManager;
import com.pypisan.kinani.storage.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeView extends Fragment implements HomeViewAdapter.SelectListener {

    // Add RecyclerView member
    private ArrayList<AnimeModel> animeRecentNum, animeTrendingList, animeTrendingListInc,
            animeRecommendList, animeRecommendListInc, animeWatchList;
    private ArrayList<ContinueWatchingModel> animeContinueWatchList;

    private RecyclerView recyclerView_trending;
    private RecyclerView recyclerView_recommend;
    private RecyclerView recyclerView_schedule;

    private RecyclerView.Adapter adapterTrending, scheduleAdapter,adapterRecommend;
    private AnimeManager animeManager;

    private ArrayList<ScheduleModel> animeScheduleList, animeScheduleListInc;
    private ShimmerFrameLayout containerTrending,containerSchedule,containerRecommend;
    private TextView recentTextHeader,triviaAnimeName,triviaAnimeEpisode,triviaAnimeReleased,
                        triviaAnimeStatus,triviaAnimeContentDetail,triviaQuestion, triviaAnimeGenre;
    private ImageView triviaAnimePic;
    private RelativeLayout watchList, fourthRelative,continueWatch,continueWatchingRelative;
    private LinearLayoutCompat triviaButtonLayout;
    private Boolean moreRecommendedVisible, moreTrendingVisible, moreWatchListVisible;
    private String triviaAnimeTitle,triviaAnimeDetail,triviaAnimeImage;

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
        moreRecommendedVisible = false;
        moreTrendingVisible = false;
        moreWatchListVisible = false;
        animeTrendingListInc = new ArrayList<>();
        animeScheduleListInc = new ArrayList<>();
        animeRecommendListInc = new ArrayList<>();

        ShimmerFrameLayout containerRecent = view.findViewById(R.id.shimmer_view_container_recent);
        ShimmerFrameLayout containerWatchList = view.findViewById(R.id.shimmer_view_container_watchlist);
        ShimmerFrameLayout containerContinueWatching = view.findViewById(R.id.shimmer_view_container_continue_watching);
        containerTrending = view.findViewById(R.id.shimmer_view_container);
        containerSchedule = view.findViewById(R.id.shimmer_view_container_schedule);
        containerRecommend = view.findViewById(R.id.shimmer_view_container_recommend);

        recentTextHeader = view.findViewById(R.id.recents);
        watchList = view.findViewById(R.id.watchListRelative);
        fourthRelative = view.findViewById(R.id.fourthRelative);
        continueWatch = view.findViewById(R.id.continueWatching);
        continueWatchingRelative = view.findViewById(R.id.continueWatchingRelative);


//      For Anime Trivia
        triviaQuestion = view.findViewById(R.id.triviaQuestion);
        triviaAnimeName = view.findViewById(R.id.triviaAnimeName);
        triviaAnimeEpisode = view.findViewById(R.id.triviaAnimeEpisode);
        triviaAnimeReleased = view.findViewById(R.id.triviaAnimeReleased);
        triviaAnimeStatus = view.findViewById(R.id.triviaAnimeStatus);
        triviaAnimeContentDetail = view.findViewById(R.id.triviaAnimeContentDetail);
        triviaAnimePic = view.findViewById(R.id.triviaAnimePhoto);
        triviaButtonLayout = view.findViewById(R.id.triviaButtonLayout);
        triviaAnimeGenre = view.findViewById(R.id.triviaAnimeGenre);

//      For More Arrow
        ImageView watchListMore = view.findViewById(R.id.watchlist_more);
        ImageView trendingMore = view.findViewById(R.id.trending_more);
        ImageView recommendationMore = view.findViewById(R.id.recommended_more);

//      Activating Animation
        LinearLayout frontCard = view.findViewById(R.id.frontView);
        LinearLayout backCard = view.findViewById(R.id.backView);
        Button seeAnswer = view.findViewById(R.id.unknown_button);
        Button watchNow = view.findViewById(R.id.watch_button);
        Button iKnow = view.findViewById(R.id.know_button);
        TextView alreadyKnow = view.findViewById(R.id.alreadyKnowText);

        seeAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(frontCard, "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(backCard, "scaleX", 0f, 1f);
                oa1.setInterpolator(new DecelerateInterpolator());
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        backCard.setVisibility(View.VISIBLE);
                        frontCard.setVisibility(View.GONE);
                        oa2.start();
                    }
                });
                oa1.start();
                oa1.setDuration(300);
                oa2.setDuration(300);
            }
        });

        watchNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked(triviaAnimeTitle,triviaAnimeDetail,triviaAnimeImage,"anime");
            }
        });

        iKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alreadyKnow.setVisibility(View.VISIBLE);
                iKnow.setVisibility(View.GONE);
                seeAnswer.setVisibility(View.GONE);
            }
        });

//      Ads
        AdView bannerAdBottom = view.findViewById(R.id.banner2);
        AdView googleAdView = view.findViewById(R.id.gadView);

//        Native Ad
        MobileAds.initialize(getContext());
        AdLoader cardAdLoader = new AdLoader.Builder(getContext(), "ca-app-pub-3251882712461623/7937761777")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        TemplateView template = view.findViewById(R.id.my_template_ad);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                    }
                })
                .build();

        AdLoader fullAdLoader = new AdLoader.Builder(getContext(), "ca-app-pub-3251882712461623/4084262778")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
//                        ColorDrawable background = ((ColorDrawable)frontCard.getBackground());
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        TemplateView template = view.findViewById(R.id.full_ad);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
//                        if (isDestroyed()) {
//                            nativeAd.destroy();
//                            return;
//                        }
                    }
                })
                .build();
        if(Constant.isFree) {
            cardAdLoader.loadAd(new AdRequest.Builder().build());
            fullAdLoader.loadAds(new AdRequest.Builder().build(), 3);
            googleAdView.loadAd(new AdRequest.Builder().build());
            bannerAdBottom.loadAd(new AdRequest.Builder().build());
        }

//      Starting Shimmer Effect
        containerRecent.startShimmer();
        containerContinueWatching.startShimmer();
        containerTrending.startShimmer();
        containerSchedule.startShimmer();
        containerRecommend.startShimmer();

//      Fetching Data
        scheduleFetcher();
        recentlyWatchedList();
        continueWatchList();
        getTrendingList();
        recommendFetcher();
        watchList();
        getTriviaAnime();

//      1st initialization recycler recent
        RecyclerView recyclerView_recent = view.findViewById(R.id.home_recycler_view_recent);
        recyclerView_recent.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView_recent.setHasFixedSize(false);

//      Setting Data
        RecyclerView.Adapter adapter = new HomeViewAdapter(animeRecentNum, getContext(), this);
        containerRecent.stopShimmer();
        containerRecent.setVisibility(View.GONE);
        recyclerView_recent.setVisibility(View.VISIBLE);
        recyclerView_recent.setItemAnimator(new DefaultItemAnimator());
        recyclerView_recent.setAdapter(adapter);

//      2nd recycler view watchlist
        RecyclerView recyclerView_watchList = view.findViewById(R.id.home_recycler_view_watchlist);
        LinearLayoutManager watchListLinearLayout = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_watchList.setLayoutManager(watchListLinearLayout);
        recyclerView_watchList.setHasFixedSize(false);
        recyclerView_watchList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = watchListLinearLayout.findLastCompletelyVisibleItemPosition();
                if (dx > 0 && !moreWatchListVisible) {
                    // Scrolled upward
                    moreWatchListVisible = true;
                    watchListMore.setVisibility(View.VISIBLE);
                }else if(firstVisibleItem < 2 && moreWatchListVisible){
                    moreWatchListVisible = false;
                    watchListMore.setVisibility(View.GONE);
                }
            }
        });

//      Setting Data
        RecyclerView.Adapter adapterWatchList = new HomeViewAdapter(animeWatchList, getContext(), this);
        containerWatchList.stopShimmer();
        containerWatchList.setVisibility(View.GONE);
        recyclerView_watchList.setVisibility(View.VISIBLE);
        recyclerView_watchList.setItemAnimator(new DefaultItemAnimator());
        recyclerView_watchList.setAdapter(adapterWatchList);


//      3rd recycler view trending
        recyclerView_trending = view.findViewById(R.id.home_recycler_view_trending);
        LinearLayoutManager trendingLinearLayout = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_trending.setLayoutManager(trendingLinearLayout);
        recyclerView_trending.setHasFixedSize(false);
        recyclerView_trending.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = trendingLinearLayout.findLastCompletelyVisibleItemPosition();
                if (dx > 0 && !moreTrendingVisible) {
                    // Scrolled upward
                    moreTrendingVisible = true;
                    trendingMore.setVisibility(View.VISIBLE);
                }else if(firstVisibleItem < 2 && moreTrendingVisible){
                    moreTrendingVisible = false;
                    trendingMore.setVisibility(View.GONE);
                }
            }
        });


//      Setting Data
        adapterTrending = new HomeViewAdapter(animeTrendingListInc, getContext(), this);

//      4th recycler view recommends
        recyclerView_recommend = view.findViewById(R.id.home_recycler_view_recommends);
        LinearLayoutManager recommendedLinearLayout = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_recommend.setLayoutManager(recommendedLinearLayout);
        recyclerView_recommend.setHasFixedSize(false);
        recyclerView_recommend.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = recommendedLinearLayout.findLastCompletelyVisibleItemPosition();
                if (dx > 0 && !moreRecommendedVisible) {
                    // Scrolled upward
                    moreRecommendedVisible = true;
                    recommendationMore.setVisibility(View.VISIBLE);
                }else if(firstVisibleItem < 2 && moreRecommendedVisible){
                    moreRecommendedVisible = false;
                    recommendationMore.setVisibility(View.GONE);
                }
            }
        });

//        Setting Data
        adapterRecommend = new HomeViewAdapter(animeRecommendListInc, getContext(), this);


//      5th recycler view aired at
        recyclerView_schedule = view.findViewById(R.id.home_recycler_view_recentlyAired);
        recyclerView_schedule.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_schedule.setHasFixedSize(false);

//      Setting Data
        scheduleAdapter = new RecentlyAiredAdapter(animeScheduleListInc, getContext(), this::onItemClicked);

//      Continue Watching Recycler
        RecyclerView recyclerView_continue = view.findViewById(R.id.home_recycler_view_continue_watching);
        recyclerView_continue.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView_continue.setHasFixedSize(false);
        RecyclerView.Adapter adapterContinue = new ContinueWatchingAdapter(animeContinueWatchList,
                getContext(), this::onContinueWatchingClicked);
        containerContinueWatching.stopShimmer();
        containerContinueWatching.setVisibility(View.GONE);
        recyclerView_continue.setVisibility(View.VISIBLE);
        recyclerView_continue.setItemAnimator(new DefaultItemAnimator());
        recyclerView_continue.setAdapter(adapterContinue);

//      More click listener

        trendingMore.setOnClickListener(v -> onClickLoadMore("trending", animeTrendingList));

        recommendationMore.setOnClickListener(v -> onClickLoadMore("recommendation", animeRecommendList));

        watchListMore.setOnClickListener(v -> onClickLoadMore("watchlist", animeWatchList));
    }

    private void continueWatchList() {
        animeContinueWatchList = new ArrayList<>();
        animeManager = new AnimeManager(getContext());
        animeManager.open();
        Cursor cursor = animeManager.readAllDataContinueWatch();
        if (cursor.getCount() != 0){
            continueWatch.setVisibility(View.VISIBLE);
            continueWatchingRelative.setVisibility(View.VISIBLE);
            ContinueWatchingModel model;
            while (cursor.moveToNext()) {
                model = new ContinueWatchingModel(cursor.getString(2),
                        cursor.getString(1), cursor.getString(3),
                        cursor.getString(5),cursor.getString(4),
                        cursor.getInt(6));
                animeContinueWatchList.add(model);
            }
            animeManager.close();
        }
    }

    private void recentlyWatchedList() {
        animeRecentNum = new ArrayList<>();
        animeManager = new AnimeManager(getContext());
        animeManager.open();
        Cursor cursor = animeManager.readAllDataRecent();
        if (cursor.getCount() != 0) {
            recentTextHeader.setVisibility(View.VISIBLE);
            AnimeModel model;
            int i = 1;
            while (cursor.moveToNext()) {
                model = new AnimeModel(cursor.getString(3), cursor.getString(1),
                        cursor.getString(2), null, cursor.getString(4),"");
                animeRecentNum.add(model);
                i++;
                if (i == 15) {
                    break;
                }
            }
            animeManager.close();
        }
    }

    private void watchList(){
        animeWatchList = new ArrayList<>();
        animeManager = new AnimeManager(getContext());
        animeManager.open();
        Cursor cursor = animeManager.readAllDataLiked();
        if (cursor.getCount() != 0){
            watchList.setVisibility(View.VISIBLE);
            fourthRelative.setVisibility(View.VISIBLE);
            AnimeModel model;
            while (cursor.moveToNext()) {
                model = new AnimeModel(cursor.getString(3),
                        cursor.getString(1), cursor.getString(2), "",
                        cursor.getString(4),"");
                animeWatchList.add(model);
            }
            animeManager.close();
        }
    }

    @Override
    public void onItemClicked(String title, String detail, String image, String type) {
        animeManager = new AnimeManager(getContext());
        animeManager.open();
        animeManager.insertRecent(detail, title, image,type);
        animeManager.close();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("image", image);
        bundle.putString("type", type);
        Fragment fragment = SummaryView.newInstance();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentView, fragment, "summary_view");
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void onContinueWatchingClicked(String title, String detail, String image, String type,
                                          String episode, String time){
        Intent i = new Intent(getContext(), VideoPlayer.class);
        i.putExtra("episode_num", episode);
        i.putExtra("title", title);
        i.putExtra("summary", detail);
        i.putExtra("server_name", "server1");
        i.putExtra("type", type);
        i.putExtra("image", image);
        i.putExtra("time",time);
        startActivity(i);
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
        String pageNum = "1";
        animeTrendingList = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule animeTrend = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call = animeTrend.getTrending(Constant.key,pageNum);

        call.enqueue(new Callback<AnimeRecentModel>() {
            @Override
            public void onResponse(Call<AnimeRecentModel> call, Response<AnimeRecentModel> response) {
                AnimeRecentModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<AnimeRecentModel.datum> data = resource.getData();
                    AnimeModel model;
                    for (AnimeRecentModel.datum animes : data) {
                        model = new AnimeModel(animes.getImageLink(),
                                animes.getAnimeDetailLink(), animes.getTitle(), animes.getReleased(),"anime","");
                        animeTrendingList.add(model);
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
        if (animeTrendingList.size() <= 15) {
            for (int i = offset; i < animeTrendingList.size(); i++) {
                animeTrendingListInc.add(animeTrendingList.get(i));
            }
            adapterTrending.notifyItemInserted(animeTrendingList.size());
        } else {
            for (int i = offset; i < 15; i++) {
                animeTrendingListInc.add(animeTrendingList.get(i));
            }
            adapterTrending.notifyItemInserted(14);
        }
    }

    //  fetching data
    private void recommendFetcher() {
        String pageNum = "1";
        animeRecommendList = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule animeRecommend = retrofit.create(RequestModule.class);
        Call<AnimeRecentModel> call = animeRecommend.getRecommend(Constant.key,pageNum);

        call.enqueue(new Callback<AnimeRecentModel>() {
            @Override
            public void onResponse(Call<AnimeRecentModel> call, Response<AnimeRecentModel> response) {
                AnimeRecentModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<AnimeRecentModel.datum> data = resource.getData();
                    AnimeModel modelRecommend;
                    for (AnimeRecentModel.datum animes : data) {
                        modelRecommend = new AnimeModel(animes.getImageLink(),
                                animes.getAnimeDetailLink(), animes.getTitle(),
                                animes.getReleased(),"anime","");
                        animeRecommendList.add(modelRecommend);
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
        if (animeRecommendList.size() <= 15) {
            for (int i = offset; i < animeRecommendList.size(); i++) {
                animeRecommendListInc.add(animeRecommendList.get(i));
            }
            adapterRecommend.notifyItemInserted(animeRecommendList.size());
        } else {
            for (int i = offset; i < 15; i++) {
                animeRecommendListInc.add(animeRecommendList.get(i));
            }
            adapterRecommend.notifyItemInserted(14);
        }
    }

    private void scheduleFetcher() {
        String pageNum = "1";
        animeScheduleList = new ArrayList<>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule animeSchedule = retrofit.create(RequestModule.class);
        Call<RecentlyAiredModel> call = animeSchedule.getAnimeSchedule(Constant.key,pageNum);

        call.enqueue(new Callback<RecentlyAiredModel>() {
            @Override
            public void onResponse(Call<RecentlyAiredModel> call, Response<RecentlyAiredModel> response) {

                RecentlyAiredModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    List<RecentlyAiredModel.datum> data = resource.getData();
                    ScheduleModel scheduleModel;
                    for (RecentlyAiredModel.datum anime : data) {
                        scheduleModel = new ScheduleModel(anime.getTitle(),
                                anime.getImage(), anime.getEpisode(), anime.getSchedule(),"anime");
                        animeScheduleList.add(scheduleModel);
                    }
                }
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

    private void onClickLoadMore(String tag, ArrayList<AnimeModel> animeList){

        Bundle bundle = new Bundle();
        bundle.putString("view", tag);
//        bundle.putParcelableArrayList("data",animeList);
        Fragment fragment = CommonGridView.newInstance();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentView, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void getTriviaAnime(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.userUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestModule animeTrivia = retrofit.create(RequestModule.class);
        Call<TriviaModel> call = animeTrivia.getTrivia(Constant.key);
        call.enqueue(new Callback<TriviaModel>() {
            @Override
            public void onResponse(Call<TriviaModel> call, Response<TriviaModel> response) {
                TriviaModel resource = response.body();
                boolean status = resource.getSuccess();
                if (status) {
                    TriviaModel.datum triviaData = resource.getData();
                    triviaAnimeImage = triviaData.getImageLink();
                    triviaAnimeTitle = triviaData.getTitle();
                    triviaAnimeDetail = triviaData.getSummary();
                    Glide.with(getContext())
                            .load(triviaAnimeImage)
                            .into(triviaAnimePic);
                    triviaQuestion.setText(triviaData.getQuestion());
                    triviaButtonLayout.setVisibility(View.VISIBLE);
                    triviaAnimeName.setText(triviaAnimeTitle);
                    triviaAnimeContentDetail.setText(triviaAnimeDetail);
                    triviaAnimeStatus.setText(String.format("Status : "+triviaData.getStatus()));
                    triviaAnimeEpisode.setText(String.format("Episodes : "+ triviaData.getEpisode_num()));
                    triviaAnimeReleased.setText(String.format("Release : "+triviaData.getReleased()));
                    String[] genres = triviaData.getGenres();
                    String formattedString = Arrays.toString(genres)
                            .replace("[", "")  //remove the right bracket
                            .replace("]", "")  //remove the left bracket
                            .trim();
                    triviaAnimeGenre.setText(String.format("Genre : "+formattedString));
                }

            }
            @Override
            public void onFailure(Call<TriviaModel> call, Throwable t) {

            }
        });
    }

}