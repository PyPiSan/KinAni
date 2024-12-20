package com.pypisan.kinani.play;

import static com.google.android.exoplayer2.ui.StyledPlayerView.SHOW_BUFFERING_ALWAYS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ext.cast.SessionAvailabilityListener;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.pypisan.kinani.R;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.api.WatchRequest;
import com.pypisan.kinani.model.EpisodeVideoModel;
import com.pypisan.kinani.storage.AnimeManager;
import com.pypisan.kinani.storage.Constant;
import java.io.File;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoPlayer extends AppCompatActivity implements SessionAvailabilityListener {

    private TextView videoHead;
    private TextView qualityText;

    private StyledPlayerView playerView;
    private boolean isFullScreen = false;
    private ExoPlayer player;

    private ImageButton fullscreen;
    private ImageButton reloadButton;
    private FrameLayout loader;
    private RelativeLayout textFrame;
    private ProgressBar videoLoading;
    private Boolean playerState = false;
    private String episode_num, type, title, summary, image, totalEpisode, location, location_type;
    private final String[] videoLink = new String[4];
    private final String[] videoDownloadLink = new String[4];

    private Long resumeTime =0L;
    private GestureDetector gestureDetector;
    private CoordinatorLayout coordinatorLayout;

    private View bottomSheet, qualityView, dlView, bottomSetting, playbackSetting;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private String currentSetQuality;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.video_coordinator);
        loader = findViewById(R.id.loader);
        reloadButton = findViewById(R.id.reloadVideo);
        videoLoading = findViewById(R.id.videoLoader);
        textFrame = findViewById(R.id.textFrame);

//      for getting video summary params
        Intent videoIntent = getIntent();
        title = videoIntent.getStringExtra("title");
        episode_num = videoIntent.getStringExtra("episode_num");
        type = videoIntent.getStringExtra("type");
        location_type = videoIntent.getStringExtra("location_type");
        if (location_type==null || location_type.equals("")){
            summary = videoIntent.getStringExtra("summary");
            totalEpisode = videoIntent.getStringExtra("total_episode");
            image = videoIntent.getStringExtra("image");
        } else{
            location = videoIntent.getStringExtra("location");
        }
        String timeValue = videoIntent.getStringExtra("time");

        if (timeValue!=null){
            resumeTime = Long.parseLong(timeValue);
        }


        TextView animeTitleView = findViewById(R.id.animeTitleText);
        TextView summaryTextView = findViewById(R.id.summaryText);
        videoHead = findViewById(R.id.videoHead);
        playerView = findViewById(R.id.video_view);
        fullscreen = findViewById(R.id.fullScreen);
        ImageButton nextButton = findViewById(R.id.nextButton);
        ImageButton previousButton = findViewById(R.id.previousButton);
        ImageButton settingButton = findViewById(R.id.setting);
        ImageButton downloadButton = findViewById(R.id.download);
        ImageButton autoPlayButton = findViewById(R.id.autoplay);
        bottomSheet = findViewById(R.id.bottom_sheet_layout);
        TextView qualityButton = findViewById(R.id.quality);
        TextView saveButton = findViewById(R.id.save_video);
        TextView lockButton = findViewById(R.id.lock_screen);
        TextView playbackSpeedButton = findViewById(R.id.playback_speed);

        qualityView = findViewById(R.id.quality_view);
        dlView = findViewById(R.id.dl_video_view);
        bottomSetting = findViewById(R.id.bottom_setting);
        playbackSetting = findViewById(R.id.speed_view);

        playerView.setShowBuffering(SHOW_BUFFERING_ALWAYS);

//      for quality setting
        qualityText = findViewById(R.id.current_quality_text);
        TextView high = findViewById(R.id.high);
        TextView medium = findViewById(R.id.medium);
        TextView avg = findViewById(R.id.avg);
        TextView low = findViewById(R.id.low);

//      for Downloading Videos
        TextView highDl = findViewById(R.id.high_save);
        TextView mediumDl = findViewById(R.id.medium_save);
        TextView avgDl = findViewById(R.id.avg_save);
        TextView lowDl = findViewById(R.id.low_save);


//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        height = displayMetrics.heightPixels;
        
//      Set up GestureDetector
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                float screenWidth = playerView.getWidth();
                float touchX = e.getX();
                long currFor = player.getContentPosition();
                if (touchX < screenWidth / 2) {
                    // Rewind
                    player.seekTo(currFor-10000);
                    simulateDoubleTapForward(findViewById(R.id.reverse_image));
                } else {
                    // Forward but do not exceed the video duration
                    player.seekTo(currFor+10000);
//                  showForwardIndicator();
                    simulateDoubleTapForward(findViewById(R.id.forward_image));
                }
                return true;
            }
        });

//      Initialize BottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Set initial state (optional)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        qualityButton.setOnClickListener(v -> {
            if (Constant.loggedInStatus && (location_type==null || location_type.equals(""))){
                bottomSetting.setVisibility(View.GONE);
                qualityView.setVisibility(View.VISIBLE);
            } else{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if ((location_type == null || location_type.equals(""))){
                    showCustomSnackBar("Log in to change the quality");
                } else{
                    showCustomSnackBar("Playing saved videos");
                }
            }
        });

        high.setOnClickListener(v -> {
            onQualitySelected(3);
            currentSetQuality="1080p";
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            qualityView.setVisibility(View.GONE);
            bottomSetting.setVisibility(View.VISIBLE);
            showCustomSnackBar(currentSetQuality + " will apply to current video");
        });
        medium.setOnClickListener(v -> {
            onQualitySelected(2);
            currentSetQuality="720p";
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            qualityView.setVisibility(View.GONE);
            bottomSetting.setVisibility(View.VISIBLE);
            showCustomSnackBar(currentSetQuality + " will apply to current video");
        });
        avg.setOnClickListener(v -> {
            onQualitySelected(1);
            currentSetQuality="480p";
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            qualityView.setVisibility(View.GONE);
            bottomSetting.setVisibility(View.VISIBLE);
            showCustomSnackBar(currentSetQuality + " will apply to current video");
        });
        low.setOnClickListener(v -> {
            onQualitySelected(0);
            currentSetQuality="360p";
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            qualityView.setVisibility(View.GONE);
            bottomSetting.setVisibility(View.VISIBLE);
            showCustomSnackBar(currentSetQuality + " will apply to current video");
        });

//      For Saving Videos

        saveButton.setOnClickListener(v -> {
            if (Constant.loggedInStatus && (location_type==null || location_type.equals(""))){
            boolean isFile= Constant.isFileExists(getApplicationContext(), Constant.formatFileName(title, episode_num, type));
            if (isFile){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showCustomSnackBar("Content is already available");
                }   else{
                    bottomSetting.setVisibility(View.GONE);
                    dlView.setVisibility(View.VISIBLE);
                }
            } else{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if ((location_type == null || location_type.equals(""))){
                    showCustomSnackBar("Log in to save the video");
                } else{
                    showCustomSnackBar("Content is already available");
                }
            }
        });

        highDl.setOnClickListener(v -> {
            boolean success = saveVideoMethod(3);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            dlView.setVisibility(View.GONE);
            bottomSetting.setVisibility(View.VISIBLE);
            if (success){
                showCustomSnackBar("Downloading, Check your phone's downloading notification");
            } else{
                showCustomSnackBar("Content not available to download");
                }
        });

        mediumDl.setOnClickListener(v -> {
            boolean success = saveVideoMethod(2);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            dlView.setVisibility(View.GONE);
            bottomSetting.setVisibility(View.VISIBLE);
            if (success){
                showCustomSnackBar("Downloading, Check your phone's downloading notification");
            }else{
                showCustomSnackBar("Content not available to download");
            }

        });

        avgDl.setOnClickListener(v -> {
            boolean success = saveVideoMethod(1);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            dlView.setVisibility(View.GONE);
            bottomSetting.setVisibility(View.VISIBLE);
            if (success){
                showCustomSnackBar("Downloading, Check your phone's downloading notification");
            }else{
                showCustomSnackBar("Content not available to download");
            }

        });

        lowDl.setOnClickListener(v -> {
            boolean success = saveVideoMethod(0);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            dlView.setVisibility(View.GONE);
            bottomSetting.setVisibility(View.VISIBLE);
            if (success){
                showCustomSnackBar("Downloading, Check your phone's downloading notification");
            }else{
                showCustomSnackBar("Content not available to download");
            }

        });


        lockButton.setOnClickListener(v -> {
            isFullScreen = checkOrientation();
            if (isFullScreen){changeOrientation(true);}
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        playbackSpeedButton.setOnClickListener(v -> {
            bottomSetting.setVisibility(View.GONE);
            playbackSetting.setVisibility(View.VISIBLE);
        });

//        for ad
        MobileAds.initialize(getApplicationContext());

        AdLoader videoAdView = new AdLoader.Builder(getApplicationContext(), "ca-app-pub-3251882712461623/8996342944")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        TemplateView template = findViewById(R.id.video_template_ad);
                        template.setVisibility(View.VISIBLE);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                    }
                })
                .build();
        if(Constant.isFree) {
            videoAdView.loadAds(new AdRequest.Builder().build(), 3);
        }

        fullscreen.setOnClickListener(v -> {
            isFullScreen = checkOrientation();
            changeOrientation(isFullScreen);
        });

        animeTitleView.setText(title);
        animeTitleView.setSelected(true);
        videoHead.setText(String.format("Episode %s", episode_num));
        videoHead.setSelected(true);
        if (location_type==null || location_type.equals("")){
            summaryTextView.setText(summary);
            getEpisodeLink(title, episode_num, type);
        } else{
            playerInitLocally(location);
        }

//      Reload Click Handler
        reloadButton.setOnClickListener(v -> {
            reloadButton.setVisibility(View.GONE);
            videoLoading.setVisibility(View.VISIBLE);
            getEpisodeLink(title, episode_num, type);
        });

//      Next Button click handler
        nextButton.setOnClickListener(v -> {
            if(!episode_num.equals(totalEpisode)){
                resumeTime =0L;
                onNextClick(title);
            }else{
                showCustomSnackBar("No next episode");
            }
        });

//      Previous Button Click handler
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!episode_num.equals("1")) {
                    resumeTime =0L;
                    onPreviousClick(title);
                }else{
//                    Toast.makeText(getApplicationContext(), "No previous episode", Toast.LENGTH_SHORT).show();
                    showCustomSnackBar("No previous episode");
                }
            }
        });

//        Setting Button Click Listener
        settingButton.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                // Expand the Bottom Sheet
//                    Set the quality text
                qualityText.setText(String.format("%s %s", getString(R.string.current_quality),
                        currentSetQuality));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                // Collapse the Bottom Sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        downloadButton.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                // Expand the Bottom Sheet
                qualityText.setText(String.format("%s %s", getString(R.string.current_quality),
                        currentSetQuality));
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                // Collapse the Bottom Sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        autoPlayButton.setOnClickListener(v -> {
//                add the function
        });

        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
//      Attach GestureDetector to PlayerView
        playerView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
//        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
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
//            showCustomSnackBar("Landscape View");
        } else {
            fullscreen.setImageResource(R.drawable.ic_fullscreen);
            textFrame.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void getEpisodeLink(String title, String episode_num, String type) {
//      fetching data
        String url;
        if ( type != null && type.equals("drama")){
            url = Constant.baseDramaUrl;
        }else{
            url = Constant.baseUrl;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestModule episodeLink = retrofit.create(RequestModule.class);
        Call<EpisodeVideoModel> call = episodeLink.getEpisodeVideo(Constant.key,
                new WatchRequest(title, episode_num, ""));
        call.enqueue(new Callback<EpisodeVideoModel>() {
            @Override
            public void onResponse(Call<EpisodeVideoModel> call, Response<EpisodeVideoModel> response) {
                boolean flag = false;
                EpisodeVideoModel resource = response.body();
                if (response.code() == 200) {
                    flag = resource.getSuccess();
                }
                if (flag) {
                    videoLink[0] = resource.getValue().getQuality1();
                    videoLink[1] = resource.getValue().getQuality2();
                    videoLink[2] = resource.getValue().getQuality3();
                    videoLink[3] = resource.getValue().getQuality4();
                    videoDownloadLink[0] = resource.getLink().dlLink1();
                    videoDownloadLink[1] = resource.getLink().dlLink2();
                    videoDownloadLink[2] = resource.getLink().dlLink3();
                    videoDownloadLink[3] = resource.getLink().dlLink4();

                }
                if (videoLink[3] != null && !videoLink[3].equals("")) {
                    playerInit(videoLink[3]);
                    currentSetQuality="1080p";
                }else if (videoLink[2] != null && !videoLink[2].equals("")) {
                    playerInit(videoLink[2]);
                    currentSetQuality="720p";
                }else if (videoLink[1] != null && !videoLink[1].equals("")) {
                    playerInit(videoLink[1]);
                    currentSetQuality="480p";
                }else if (videoLink[0] != null && !videoLink[0].equals("")) {
                    playerInit(videoLink[0]);
                    currentSetQuality="360p";
                }
                else{
                    videoLoading.setVisibility(View.GONE);
                    reloadButton.setVisibility(View.VISIBLE);
                    showCustomSnackBar("Not found, Click Retry");
                }
            }

            @Override
            public void onFailure(Call<EpisodeVideoModel> call, Throwable t) {
                videoLoading.setVisibility(View.GONE);
                reloadButton.setVisibility(View.VISIBLE);
                showCustomSnackBar("Not found, Click Retry");
            }
        });

    }

    public void playerInit(String link) {
//        loader.setVisibility(View.GONE);
        videoLoading.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
        showCustomSnackBar("Now Playing Episode: " +episode_num);
        Uri hlsUri = Uri.parse(link);
//        int flags = DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
//                    | DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS;
//            DefaultHlsExtractorFactory extractorFactory = new DefaultHlsExtractorFactory(flags, true);

//          New Implementation
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            dataSourceFactory.setAllowCrossProtocolRedirects(true);
            dataSourceFactory.setUserAgent("curl/7.85.0");
            dataSourceFactory.setConnectTimeoutMs(10000);

            HlsMediaSource.Factory hlsMediaSource =
                    new HlsMediaSource.Factory(dataSourceFactory);
//                        .setExtractorFactory(extractorFactory);


//          Create a player instance.
            player = new ExoPlayer.Builder(this)
                    .setMediaSourceFactory(hlsMediaSource)
                    .build();
            // Set the media source to be played.
            // Prepare the player.
            playerView.setPlayer(player);
            player.setMediaItem(MediaItem.fromUri(hlsUri));
            player.prepare();
            if (resumeTime>0){
                player.seekTo(resumeTime);
            }
            player.play();
            playerState = true;
    }

    public void playerInitLocally(String location){
        videoLoading.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
        showCustomSnackBar("Now Playing Episode: " +episode_num);
        Uri videoUri = Uri.fromFile(new File(location));
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
//      Create a player instance.
        player = new ExoPlayer.Builder(this)
                .build();
        playerView.setPlayer(player);
        player.setMediaItem(mediaItem);
        player.prepare();
        if (resumeTime>0){
            player.seekTo(resumeTime);
        }
        player.play();
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
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) loader.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            loader.setLayoutParams(layoutParams);
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            getWindow().clearFlags(View.KEEP_SCREEN_ON);

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) loader.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = (int) getResources().getDimension(R.dimen.video_width);
            loader.setLayoutParams(layoutParams);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                  Reset the View
                    bottomSetting.setVisibility(View.VISIBLE);
                    playbackSetting.setVisibility(View.GONE);
                    dlView.setVisibility(View.GONE);
                    qualityView.setVisibility(View.GONE);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        changeOrientation(false);
        long time = 0;
        long videoLength = 0;
        if(playerState) {
            time = player.getContentPosition();
            videoLength = player.getContentDuration();
            player.stop();
            player.release();
        }
        if (time<(videoLength-300000) && time>60000){
            AnimeManager animeManager= new AnimeManager(getApplicationContext());;
            animeManager.open();
            animeManager.insertContinueWatch(summary, title, image, type,episode_num,(int) time);
            animeManager.close();
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
        videoHead.setText(String.format("Episode %s", episode_num));
        videoHead.setSelected(true);
        loader.setVisibility(View.VISIBLE);
        reloadButton.setVisibility(View.GONE);
        videoLoading.setVisibility(View.VISIBLE);
        getEpisodeLink(title, episode_num, type);
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
        videoHead.setText(String.format("Episode %s", episode_num));
        videoHead.setSelected(true);
        loader.setVisibility(View.VISIBLE);
        reloadButton.setVisibility(View.GONE);
        videoLoading.setVisibility(View.VISIBLE);
        getEpisodeLink(title, episode_num, type);
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
        player.play();
    }
    @Override
    public void onCastSessionAvailable() {

    }

    @Override
    public void onCastSessionUnavailable() {

    }





//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Toast.makeText(getApplicationContext(),"Touch clicked ", Toast.LENGTH_SHORT).show();
//        new ScaleGestureDetector(this, new MySimpleOnScaleGestureListener(playerView,getApplicationContext()))
//                .onTouchEvent(event);
//        return true;
//        //return super.onTouchEvent(event);
//    }


    private static class MySimpleOnScaleGestureListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        float scaleFactor = 0f;
        StyledPlayerView playerMode;
        Context v;

        public MySimpleOnScaleGestureListener(StyledPlayerView playerMode, Context view) {
            super();
            this.playerMode = playerMode;
            this.v = view;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor = detector.getScaleFactor();
            return true;
            //return super.onScale(detector);
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
            Toast.makeText(v,"scale factor is "+ scaleFactor, Toast.LENGTH_LONG).show();
            if (scaleFactor > 1) {
                playerMode.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            } else {
                playerMode.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            }
        }
    }

//    private void showCustomToast(String message) {
//        LayoutInflater inflater = getLayoutInflater();
//        View layout = inflater.inflate(R.layout.toast_layout,
//                (ViewGroup) findViewById(R.id.toast_layout_container));
//        TextView toastTextView = layout.findViewById(R.id.toast_message);
//
//        //change text view
//        toastTextView.setText(message);
//        Toast toast = new Toast(getApplicationContext());
//        toast.setView(layout);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.show();
//    }

    private void showCustomSnackBar(String message){
//      For custom Snack bar
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryToastColor));
        TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.primaryToastTextColor));
        textView.setTextSize(14);
        snackbar.show();
    }

    private void simulateDoubleTapForward(ImageView forwardImage) {
        // Load the scale animation
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.fast_farward);

        // Show the TextView and start the animation
        forwardImage.setVisibility(View.VISIBLE);
        forwardImage.startAnimation(scaleAnimation);

        // Hide the TextView after the animation ends
        new Handler().postDelayed(() -> forwardImage.setVisibility(View.GONE), 600);
    }

    private Boolean saveVideoMethod(int index){
        String link = videoDownloadLink[index];
        if (link != null && !link.equals("")){
            DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(link);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            String name = Constant.formatFileName(title, episode_num, type);
            request.setTitle(name);
            request.setDescription("Downloading");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setVisibleInDownloadsUi(true);
            File file = new File(getExternalFilesDir(Constant.storageLocation), name);
            Uri fileUri = Uri.fromFile(file);
            request.setDestinationUri(fileUri);
            downloadmanager.enqueue(request);
            return true;
        }
        return false;
    }
}
