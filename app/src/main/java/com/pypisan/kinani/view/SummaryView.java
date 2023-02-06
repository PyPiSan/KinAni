package com.pypisan.kinani.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.pypisan.kinani.R;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.model.AnimeEpisodeListModel;
import com.pypisan.kinani.model.Jtitle;
import com.pypisan.kinani.play.VideoPlayer;
import com.pypisan.kinani.storage.AnimeManager;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SummaryView extends Fragment {

    private ImageView headImage, titleImage;
    private TextView title, summary, releasedValue, statusValue;
    private ListView episodes;
    private AutoCompleteTextView autoCompleteText;
    private ArrayAdapter<String> episodeAdapter;
    private AnimeEpisodeListModel.datum animeDetail;
    private AnimeManager animeManager;
    private String animetitle;
    private String animeLink;
    private ShimmerFrameLayout containerImg, containerSummaryText, conatinerImgHead;
    private Animation animationImage;
    private boolean isTextViewClicked = false;
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

//        likedButton = view.findViewById(R.id.likeButton);
//        dislikeButton = view.findViewById(R.id.dislikeButton);
        animeManager = new AnimeManager(getContext());

//        for shimmer effect
        conatinerImgHead = view.findViewById(R.id.shimmer_view_animePic);
        containerImg = view.findViewById(R.id.shimmer_view_titleImage);
        containerSummaryText = view.findViewById(R.id.shimmer_view_summary_text);

        conatinerImgHead.startShimmer();
        containerImg.startShimmer();
        containerSummaryText.startShimmer();

//        Fetching Anime Detail Summary
        getAnimeSummary(view);

//        For animation
        animationImage = AnimationUtils.loadAnimation(getContext(), R.anim.summary_image);


//        Add to liked button click listener
//        animeManager.open();
//        likedButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                animeManager.insert(jtitle, animetitle, animeLink);
//                Toast.makeText(getContext(), "Anime added to Liked", Toast.LENGTH_LONG).show();
//                animeManager.close();
//            }
//        });
//        dislikeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                animeManager.delete(jtitle);
//                Toast.makeText(getContext(), "Anime Removed", Toast.LENGTH_LONG).show();
//                animeManager.close();
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.summary_view, container, false);
    }

    private void getAnimeSummary(View view) {
        headImage = view.findViewById(R.id.animePic);
        titleImage = view.findViewById(R.id.animePicTitle);
        title = view.findViewById(R.id.titleName);
        summary = view.findViewById(R.id.summaryText);
        episodes = view.findViewById(R.id.episodeList);
        releasedValue = view.findViewById(R.id.releasedVal);
        statusValue = view.findViewById(R.id.statusVal);
        autoCompleteText = view.findViewById(R.id.autoCompleteTextView);


//      fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anime.pypisan.com/v1/anime/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String animeName = getArguments().getString("title");
//        Log.d("E0", "jTitle is : " + animeName);
        Toast.makeText(getContext(), animeName, Toast.LENGTH_LONG).show();
        RequestModule animeEpisode = retrofit.create(RequestModule.class);
        Call<AnimeEpisodeListModel> call = animeEpisode.getEpisodeList(new Jtitle(animeName));
        call.enqueue(new Callback<AnimeEpisodeListModel>() {
            @Override
            public void onResponse(Call<AnimeEpisodeListModel> call, Response<AnimeEpisodeListModel> response) {
                boolean flag = false;
//                Log.d("E1", "Response code is : " + response.code());
                AnimeEpisodeListModel resource = response.body();
                if (response.code() == 200) {
                    boolean status = resource.getSuccess();
//                    Log.d("E2", "status is " + status);
                    flag = status;
                }
                if (flag) {
                    animeDetail = resource.getData();
//                    Log.d("E2", "Response is " + animeDetail.getTitle());

                    animetitle = animeDetail.getTitle();
                    animeLink = animeDetail.getImageLink();
//                    int randNum = ThreadLocalRandom.current().nextInt(0, 10);
                    conatinerImgHead.stopShimmer();
                    Glide.with(getContext())
                            .load(animeLink)
                            .into(headImage);

//                  stopping shimmer effect
                    conatinerImgHead.setVisibility(View.GONE);
                    headImage.setVisibility(View.VISIBLE);
//                    headImage.startAnimation(animationImage);
//                    containerImg.stopShimmer();

//                    Adding data to view
                    Glide.with(getContext())
                            .load(animeLink)
                            .into(titleImage);
                    containerImg.setVisibility(View.GONE);
                    titleImage.setVisibility(View.VISIBLE);

                    title.setText(animetitle);
                    releasedValue.setText(animeDetail.getReleased());
                    statusValue.setText(animeDetail.getStatus());
                    containerSummaryText.stopShimmer();
                    summary.setText(animeDetail.getSummary());
                    containerSummaryText.setVisibility(View.GONE);
                    summary.setVisibility(View.VISIBLE);
                    summary.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isTextViewClicked){
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
//                    Creating drop down for episodes

                    String episodeNums = animeDetail.getEpisode_num();
                    if (episodeNums.equals("0")){
                        episodeNums="1";
                    }
                    int episodeNumVal = Integer.parseInt(episodeNums);
//                    Log.d("E10", "episode_number is " + episodeNumVal);
                    String[] episodes = new String[episodeNumVal];
                    for (int i = 0; i < episodeNumVal; i++) {
                        episodes[i] = "Episode: " + (i + 1);
                    }
                    Log.d("E11", "episode_number is " + Arrays.toString(episodes));
                    episodeAdapter = new ArrayAdapter<String>(getContext(), R.layout.drop_down_list, episodes);
                    autoCompleteText.setAdapter(episodeAdapter);
                    Log.d("E12", "Till Success Here");

//                    Setting on click listener
                    autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            String item = adapterView.getItemAtPosition(position).toString();
                            Intent i = new Intent(getContext(), VideoPlayer.class);
                            i.putExtra("episode_num", String.valueOf(position + 1));
                            i.putExtra("title", animetitle);
                            i.putExtra("summary", animeDetail.getSummary());
//                            i.putExtra("jTitle", jtitle);
                            i.putExtra("server_name", "server1");
                            startActivity(i);
                        }
                    });


                } else {
                    Toast.makeText(getContext(), "Anime Not Found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AnimeEpisodeListModel> call, Throwable t) {
                Log.d("E3", "Response code is : 400" + t.getMessage());
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        getActivity().getWindow()
                .setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

}