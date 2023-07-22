package com.pypisan.kinani.view;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inmobi.ads.InMobiBanner;
import com.pypisan.kinani.R;
import com.pypisan.kinani.adapter.EpisodeAdapter;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.model.AnimeEpisodeListModel;
import com.pypisan.kinani.model.Jtitle;
import com.pypisan.kinani.play.VideoPlayer;
import com.pypisan.kinani.storage.AnimeManager;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SummaryView extends Fragment{

    private ImageView headImage, titleImage;
    private TextView title, summary, releasedValue, statusValue, genreVal, aboutTextHead;
    private ListView episodes;

    private Button retryButton;
    private ArrayAdapter<String> episodeAdapter;
    private AnimeEpisodeListModel.datum animeDetail;
    private AnimeManager animeManager;
    private String animetitle, animeDetailLink, animeLink;
    private ShimmerFrameLayout containerImg, containerSummaryText, containerImgHead;
    private Animation animationImage;
    private InMobiBanner bannerAd;
    private CardView cardImageTitle, cardHeadImage;

    private AppCompatSpinner episodeSpinner;
    private RelativeLayout ratingLayout, errorView;
    private boolean isTextViewClicked = false;
    private LottieAnimationView likedFab;
    private Cursor cursor = null;
    public SummaryView() {
        // Required empty public constructor
    }

    public static SummaryView newInstance() {
        return new SummaryView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        bg = new int[]{R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4,
//                R.drawable.bg5, R.drawable.bg6, R.drawable.bg7, R.drawable.bg8,
//                R.drawable.bg9, R.drawable.bg10};
//            int[] anim = new int[]{R.raw.liked};
        String animeName = getArguments().getString("title");
        animeManager = new AnimeManager(getContext());

//        recyclerEpisode = view.findViewById(R.id.episodeRecycler);
//        recyclerEpisode.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        recyclerEpisode.setHasFixedSize(true);

//        adapterEpisode = new EpisodeAdapter(getContext(), 300, this);
//        recyclerEpisode.setAdapter(adapterEpisode);
//        for shimmer effect
        containerImgHead = view.findViewById(R.id.shimmer_view_animePic);
        containerImg = view.findViewById(R.id.shimmer_view_titleImage);
        containerSummaryText = view.findViewById(R.id.shimmer_view_summary_text);

        containerImgHead.startShimmer();
        containerImg.startShimmer();
        containerSummaryText.startShimmer();

//        error view after timeout
        errorView = view.findViewById(R.id.errorView);
        aboutTextHead = view.findViewById(R.id.about);

//        retry logic page reloading
        retryButton = view.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                errorView.setVisibility(View.GONE);
                containerImg.setVisibility(View.VISIBLE);
                containerSummaryText.setVisibility(View.VISIBLE);
                containerImgHead.setVisibility(View.VISIBLE);
                aboutTextHead.setVisibility(View.VISIBLE);
                getAnimeSummary(view, animeName);
                bannerAd.load();
            }
        });

//        Ads
        bannerAd = (InMobiBanner)view.findViewById(R.id.banner);
//        Fetching Anime Detail Summary
        getAnimeSummary(view, animeName);
        bannerAd.load();

//        For animation
        animationImage = AnimationUtils.loadAnimation(getContext(), R.anim.summary_image);

//        For FAB Like
        likedFab = view.findViewById(R.id.ActionButton);


//        Add to liked button click listener
        animeManager.open();
        cursor = animeManager.findOne(animeName);
        if (cursor != null && cursor.getCount() != 0){
//            likedFab.setImageResource(R.drawable.liked_button);
            likedFab.setAnimation(R.raw.liked);
            likedFab.playAnimation();
        }
        animeManager.close();
        likedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animeManager.open();
                cursor = animeManager.findOne(animeName);
                if (cursor == null || cursor.getCount() == 0) {
                    animeManager.insertLiked(animeDetailLink, animetitle, animeLink);
                    likedFab.setAnimation(R.raw.liked);
                    likedFab.playAnimation();
                    Toast.makeText(getContext(), "Added to Liked", Toast.LENGTH_SHORT).show();
                }else{
                    animeManager.deleteLiked(animetitle);
                    likedFab.setAnimation(R.raw.heart);
                    likedFab.playAnimation();
                    Toast.makeText(getContext(), "Removed from Liked", Toast.LENGTH_SHORT).show();
                }
                animeManager.close();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.summary_view, container, false);
    }

    private void getAnimeSummary(View view, String animeName) {
        headImage = view.findViewById(R.id.animePic);
        titleImage = view.findViewById(R.id.animePicTitle);
        title = view.findViewById(R.id.titleName);
        summary = view.findViewById(R.id.summaryText);
        episodes = view.findViewById(R.id.episodeList);
        releasedValue = view.findViewById(R.id.releasedVal);
        statusValue = view.findViewById(R.id.statusVal);
        genreVal = view.findViewById(R.id.genreVal);
        cardImageTitle = view.findViewById(R.id.cardImageTitle);
        cardHeadImage = view.findViewById(R.id.cardHeadImage);
        episodeSpinner = view.findViewById(R.id.episodeSpinner);
        ratingLayout = view.findViewById(R.id.ratingLayout);

//      fetching data
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anime.pypisan.com/v1/anime/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        RequestModule animeEpisode = retrofit.create(RequestModule.class);
        Call<AnimeEpisodeListModel> call = animeEpisode.getEpisodeList(new Jtitle(animeName));
        call.enqueue(new Callback<AnimeEpisodeListModel>() {
            @Override
            public void onResponse(Call<AnimeEpisodeListModel> call, Response<AnimeEpisodeListModel> response) {
                boolean flag = false;
                AnimeEpisodeListModel resource = response.body();
                if (response.code() == 200) {
                    boolean status = resource.getSuccess();
                    flag = status;
                }
                if (flag) {
                    animeDetail = resource.getData();
//                    Log.d("E2", "Response is " + animeDetail.getTitle());

                    animetitle = animeDetail.getTitle();
                    animeLink = animeDetail.getImageLink();
                    animeDetailLink = animeDetail.getAnimeDetailLink();
//                    int randNum = ThreadLocalRandom.current().nextInt(0, 10);
                    containerImgHead.stopShimmer();
                    Glide.with(getContext())
                            .load(animeLink)
                            .into(headImage);

//                  stopping shimmer effect
                    containerImgHead.setVisibility(View.GONE);
                    cardHeadImage.setVisibility(View.VISIBLE);
                    containerImg.stopShimmer();

//                  Adding data to view
                    Glide.with(getContext())
                            .load(animeLink)
                            .into(titleImage);
                    containerImg.setVisibility(View.GONE);
                    cardImageTitle.setVisibility(View.VISIBLE);

                    title.setText(animetitle);
                    releasedValue.setText(animeDetail.getReleased());
                    statusValue.setText(animeDetail.getStatus());
                    containerSummaryText.stopShimmer();
                    summary.setText(animeDetail.getSummary());
                    containerSummaryText.setVisibility(View.GONE);
                    summary.setVisibility(View.VISIBLE);
                    ratingLayout.setVisibility(View.VISIBLE);
                    String[] genres = animeDetail.getGenres();
                    String formattedString = Arrays.toString(genres)
                            .replace("[", "")  //remove the right bracket
                            .replace("]", "")  //remove the left bracket
                            .trim();
                    genreVal.setText(formattedString);
                    summary.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isTextViewClicked) {
                                //This will shrink textview to 2 lines if it is expanded.
                                summary.setMaxLines(5);
                                isTextViewClicked = false;
                            } else {
                                //This will expand the textview if it is of 2 lines
                                summary.setMaxLines(Integer.MAX_VALUE);
                                isTextViewClicked = true;
                            }
                        }
                    });

                    if(!animeDetail.getStatus().equalsIgnoreCase("upcoming")){
                        episodeSpinner.setVisibility(View.VISIBLE);
//                    Creating drop down for episodes
                    String episodeNum = animeDetail.getEpisode_num();
                    if (episodeNum.equals("1")) {
                        episodeNum = "2";
                    }
                    int episodeNumVal = Integer.parseInt(episodeNum);
//                    Log.d("E10", "episode_number is " + episodeNumVal);
                    String[] episodes = new String[episodeNumVal];
                    episodes[0] = "Select An Episode";
                    for (int i = 1; i < episodeNumVal; i++) {
                        episodes[i] = "Episode: " + i;
                    }
                    episodeAdapter = new ArrayAdapter<String>(getContext(), R.layout.drop_down_list, episodes);
                    episodeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                    episodeSpinner.setAdapter(episodeAdapter);
                    int initialSelectedPosition = episodeSpinner.getSelectedItemPosition();
                    episodeSpinner.setSelection(initialSelectedPosition, false);
//                    Setting on click listener
                    episodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            String item = adapterView.getSelectedItem().toString();
                            if (position != 0) {
                                Intent i = new Intent(getContext(), VideoPlayer.class);
                                i.putExtra("episode_num", String.valueOf(position));
                                i.putExtra("title", animetitle);
                                i.putExtra("summary", animeDetail.getSummary());
                                i.putExtra("server_name", "server1");
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }

                } else {
                    Toast.makeText(getContext(), "Anime Not Found", Toast.LENGTH_LONG).show();
                    bannerAd.destroy();
                    errorView.setVisibility(View.VISIBLE);
                    containerImg.setVisibility(View.GONE);
                    containerSummaryText.setVisibility(View.GONE);
                    containerImgHead.setVisibility(View.GONE);
                    aboutTextHead.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<AnimeEpisodeListModel> call, Throwable t) {
                bannerAd.destroy();
                Toast.makeText(getContext(), "Anime Not Found", Toast.LENGTH_LONG).show();
                errorView.setVisibility(View.VISIBLE);
                containerImg.setVisibility(View.GONE);
                containerSummaryText.setVisibility(View.GONE);
                containerImgHead.setVisibility(View.GONE);
                aboutTextHead.setVisibility(View.GONE);
            }
        });

    }
//    @Override
//    public void onEpisodeClicked(int num){
//        Intent i = new Intent(getContext(), VideoPlayer.class);
//        i.putExtra("episode_num", String.valueOf(num));
//        i.putExtra("title", animetitle);
//        i.putExtra("summary", animeDetail.getSummary());
//        i.putExtra("server_name", "server1");
//        startActivity(i);
//    }
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//        getActivity().getWindow()
//                .setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        bannerAd.destroy();
//        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

}