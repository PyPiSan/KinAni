package com.pypisan.kinani.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.pypisan.kinani.R;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.api.UserInit;
import com.pypisan.kinani.model.UserModel;
import com.pypisan.kinani.storage.AnimeManager;
import com.pypisan.kinani.storage.Constant;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeSplash extends AppCompatActivity {
    private  AnimeManager animeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_splash);
        ProgressBar loader = findViewById(R.id.loader);
        animeManager = new AnimeManager(getApplicationContext());
//        JSONObject consentObject = new JSONObject();

//        Google Ads

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        @SuppressLint("HardwareIds") String deviceUser = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String myVersion = "Android "+Build.VERSION.RELEASE;
        Constant.uid = deviceUser;
        String origin = getApplicationContext().getResources().getConfiguration().locale.getCountry();

//        Cursor cursor = animeManager.findOneUser(deviceUser);
//        if (cursor != null && cursor.getCount() != 0){
//            while (cursor.moveToNext()) {
//                Constant.key=cursor.getString(2);
//                Constant.logo =cursor.getInt(5);
//                if (cursor.getInt(4) > 0){
//                    Constant.loggedInStatus = true;
//                }else{
//                    Constant.loggedInStatus = false;
//                }
//            }
//            animeManager.close();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(HomeSplash.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }, 1000);
//
//        }else {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.userUrl)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            RequestModule getID = retrofit.create(RequestModule.class);
            Call<UserModel> call = getID.getUser(new UserInit(deviceUser, origin,myVersion));
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    boolean flag = false;
                    UserModel resource = response.body();
                    if (response.code() == 200) {
                        flag = resource.getUserStatus();
                        Constant.key=resource.getApikey();
                    }
                    if (flag) {
                        animeManager.open();
                        if (resource.getLogged()){
                            Constant.loggedInStatus = true;
                            Constant.logo =resource.getIcon();
                            Constant.userName = resource.getUserData();
                            Constant.message = resource.getNotification();
                            if (resource.getAds() !=null){Constant.isFree = resource.getAds();}
                            try {
                                PackageInfo pInfo = getApplicationContext().getPackageManager().
                                        getPackageInfo(getApplicationContext().getPackageName(), 0);
                                Constant.versionName = pInfo.versionName;
                            } catch (PackageManager.NameNotFoundException ignored) {
                            }
                            animeManager.insertUser(deviceUser, Constant.key, true,resource.getLogged(),resource.getIcon());
                            Cursor newCursor = animeManager.findOneUser(deviceUser);
                            if (newCursor != null && newCursor.getCount() != 0){
                                while (newCursor.moveToNext()) {
                                    if (newCursor.getString(6) != null){
                                        if (!Constant.message.replaceAll("[\n\r\t,.'@/ ]","").equals
                                                (newCursor.getString(6).replaceAll("[\n\r\t,.'@/ ]","")))
                                        {
                                            Constant.isMessage = true;
                                        }
                                    }else{
                                        Constant.isMessage = true;
                                    }
                                }
                            }

                        }else{
                            Constant.loggedInStatus = false;
                            animeManager.insertUser(deviceUser, Constant.key, true,resource.getLogged(),0);
                        }
                        animeManager.close();
                        loader.setVisibility(View.GONE);
                        Intent intent = new Intent(HomeSplash.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        getAppAbout();
//                      Message Call end
                    } else {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Failed, Try Again", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Server Down", Toast.LENGTH_LONG).show();
                }
            });
//        }


    }

    private void getAppAbout(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.userUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
        RequestModule getAbout = retrofit.create(RequestModule.class);
        Call<UserModel> call = getAbout.getAppAbout(Constant.key);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                boolean flag = false;
                UserModel resource = response.body();
                if (response.code() == 200) {
                    Constant.about = resource.getMessage();
                }
            }
            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
            }
        });
    }

}