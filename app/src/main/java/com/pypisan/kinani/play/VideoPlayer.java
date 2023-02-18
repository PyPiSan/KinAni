package com.pypisan.kinani.play;

import static com.google.android.exoplayer2.ui.StyledPlayerView.SHOW_BUFFERING_ALWAYS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.mediarouter.app.MediaRouteButton;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.pypisan.kinani.R;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.api.WatchRequest;
import com.pypisan.kinani.model.EpisodeVideoModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoPlayer extends AppCompatActivity implements SessionAvailabilityListener {

    private TextView animeTitleView, summaryTextView;
    private StyledPlayerView playerView;
    private boolean isFullScreen = false;
    private ExoPlayer player;
    private Uri hlsUri;
    private CastContext mCastContext;
    private MediaRouteButton mMediaRouteButton;
    private ImageButton fullscreen, nextButton, reloadButton, previousButton, settingButton;
    private FrameLayout loader, textFrame, settingPopup;
    private ProgressBar videoLoading;
    private Boolean playerState = false, visibility = false;;
    private String episode_num;
    private String[] videoLink = new String[4];

    private Button qualityOne, qualityTwo, qualityThree, qualityFour;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        loader = findViewById(R.id.loader);
        reloadButton = findViewById(R.id.reloadVideo);
        videoLoading = findViewById(R.id.videoLoader);
        textFrame = findViewById(R.id.textFrame);
//        for getting video summary params
        Intent videoIntent = getIntent();

        String title = videoIntent.getStringExtra("title");
        String summary = videoIntent.getStringExtra("summary");
        episode_num = videoIntent.getStringExtra("episode_num");


        animeTitleView = findViewById(R.id.animeTitleText);
        summaryTextView = findViewById(R.id.summaryText);
        playerView = findViewById(R.id.video_view);
        fullscreen = findViewById(R.id.fullScreen);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        settingButton = findViewById(R.id.setting);

        Dialog settingDialog = new Dialog(this);
        settingDialog.setContentView(R.layout.video_quality_dailog);
        qualityOne = settingDialog.findViewById(R.id.qualityOne);
        qualityTwo = settingDialog.findViewById(R.id.qualityTwo);
        qualityThree = settingDialog.findViewById(R.id.qualityThree);
        qualityFour = settingDialog.findViewById(R.id.qualityFour);

        playerView.setShowBuffering(SHOW_BUFFERING_ALWAYS);
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFullScreen = checkOrientation();
                changeOrientation(isFullScreen);
            }
        });
        animeTitleView.setText(title);
        summaryTextView.setText(summary);
        getEpisodeLink(title, episode_num);

//        for casting video
        mCastContext = CastContext.getSharedInstance(this);
        mMediaRouteButton = (MediaRouteButton) findViewById(R.id.cast);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mMediaRouteButton);

        if(mCastContext.getCastState() != CastState.NO_DEVICES_AVAILABLE)
            mMediaRouteButton.setVisibility(View.VISIBLE);

        mCastContext.addCastStateListener(new CastStateListener() {
            @Override
            public void onCastStateChanged(int state) {
                if (state == CastState.NO_DEVICES_AVAILABLE)
                    mMediaRouteButton.setVisibility(View.GONE);
                else {
                    if (mMediaRouteButton.getVisibility() == View.GONE)
                        mMediaRouteButton.setVisibility(View.VISIBLE);
                }
            }
        });

        final CastPlayer castPlayer = new CastPlayer(mCastContext);
        castPlayer.setSessionAvailabilityListener(new SessionAvailabilityListener() {
            @Override
            public void onCastSessionAvailable() {

            }

            @Override
            public void onCastSessionUnavailable() {

            }
        });

//        Reload Click Handler
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                reloadButton.setVisibility(View.GONE);
                videoLoading.setVisibility(View.VISIBLE);
                getEpisodeLink(title, episode_num);
            }
        });

//        Next Button click handler
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClick(title);
            }
        });

//        Previous Button Click handler
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreviousClick(title);
            }
        });

//        Setting Button Click Listener
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog.show();
//                if (visibility){
//                    settingDailog.cancel();
//                    visibility = false;
//                }else
//                {
//                    settingDailog.show();
//                    visibility = true;
//                }
            }
        });
        qualityOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQualitySelected(0);
                settingDialog.cancel();
            }
        });
        qualityTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQualitySelected(1);
                settingDialog.cancel();
            }
        });

        qualityThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQualitySelected(2);
                settingDialog.cancel();
            }
        });
        qualityFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onQualitySelected(3);
                settingDialog.cancel();
            }
        });
    }


//    Check Orientation
//    returns true if in portrait mode

    private boolean checkOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation != Configuration.ORIENTATION_LANDSCAPE;
    }

//   Change Orientation

    public void changeOrientation(boolean shouldLandscape) {
        if (shouldLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fullscreen.setImageResource(R.drawable.fullscreen_close);
            textFrame.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Landscape View", Toast.LENGTH_SHORT).show();
        } else {
            fullscreen.setImageResource(R.drawable.ic_fullscreen);
            textFrame.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void getEpisodeLink(String title, String episode_num) {
        //      fetching data
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anime.pypisan.com/v1/anime/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestModule episodeLink = retrofit.create(RequestModule.class);
        Call<EpisodeVideoModel> call = episodeLink.getEpisodeVideo(new WatchRequest(title, episode_num, ""));
        call.enqueue(new Callback<EpisodeVideoModel>() {
            @Override
            public void onResponse(Call<EpisodeVideoModel> call, Response<EpisodeVideoModel> response) {
                boolean flag = false;
                EpisodeVideoModel resource = response.body();
                if (response.code() == 200) {
                    boolean status = resource.getSuccess();
                    flag = status;
                }
//                Log.d("video", "link is"+resource.getSuccess());
                if (flag) {
                    videoLink[0] = resource.getValue().getQuality1();
                    videoLink[1] = resource.getValue().getQuality2();
                    videoLink[2] = resource.getValue().getQuality3();
                    videoLink[3] = resource.getValue().getQuality4();
                }
                if (videoLink[2] == null || videoLink[2].equals("")) {
                    videoLoading.setVisibility(View.GONE);
                    reloadButton.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Not found, Click Retry", Toast.LENGTH_LONG).show();
                }else{
                    playerInit(videoLink[2]);
                }
            }

            @Override
            public void onFailure(Call<EpisodeVideoModel> call, Throwable t) {
                videoLoading.setVisibility(View.GONE);
                reloadButton.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Not found, Click Retry", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void playerInit(String link) {
        loader.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Video found, Ep: " +episode_num, Toast.LENGTH_SHORT).show();
        hlsUri = Uri.parse(link);
        int flags = DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
                    | DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS;
            DefaultHlsExtractorFactory extractorFactory = new DefaultHlsExtractorFactory(flags, true);

//        New Implementation
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            dataSourceFactory.setAllowCrossProtocolRedirects(true);
            dataSourceFactory.setUserAgent("curl/7.85.0");
            dataSourceFactory.setConnectTimeoutMs(10000);

            HlsMediaSource.Factory hlsMediaSource =
                    new HlsMediaSource.Factory(dataSourceFactory);
//                        .setExtractorFactory(extractorFactory);


            // Create a player instance.
            player = new ExoPlayer.Builder(this)
                    .setMediaSourceFactory(hlsMediaSource)
                    .build();
            // Set the media source to be played.
            // Prepare the player.
            playerView.setPlayer(player);
            player.setMediaItem(MediaItem.fromUri(hlsUri));
            player.prepare();
            playerState = true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            playerView.setLayoutParams(layoutParams);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            getWindow().clearFlags(View.KEEP_SCREEN_ON);

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = (int) 242*3;
            playerView.setLayoutParams(layoutParams);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        changeOrientation(false);
        if(playerState) {
            player.stop();
            player.release();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        changeOrientation(false);
//        if(playerState) {
//            player.stop();
//            player.release();
//        }
//    }

    private void onNextClick(String title){
        if(playerState) {
            player.stop();
            player.release();
        }
        int num = Integer.parseInt(episode_num);
        episode_num = String.valueOf(num+1);
        playerView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        reloadButton.setVisibility(View.GONE);
        videoLoading.setVisibility(View.VISIBLE);
        getEpisodeLink(title, episode_num);
    }

    private void onPreviousClick(String title){
        if(playerState) {
            player.stop();
            player.release();
        }
        int num = Integer.parseInt(episode_num);
        if (num == 1){
            episode_num = String.valueOf(num);
        }else{
            episode_num = String.valueOf(num-1);
        }
        playerView.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        reloadButton.setVisibility(View.GONE);
        videoLoading.setVisibility(View.VISIBLE);
        getEpisodeLink(title, episode_num);
    }

    private void onQualitySelected(int index){
        long time = 0;
        if(playerState) {
            player.pause();
            time = player.getContentPosition();
        }
        String newLink = videoLink[index];
        player.setMediaItem(MediaItem.fromUri(Uri.parse(newLink)));
        player.prepare();
        player.seekTo(time);
    }
    @Override
    public void onCastSessionAvailable() {

    }

    @Override
    public void onCastSessionUnavailable() {

    }
}
