package com.pypisan.kinani.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.pypisan.kinani.R;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.api.UserInit;
import com.pypisan.kinani.api.UserUpdate;
import com.pypisan.kinani.model.UserModel;
import com.pypisan.kinani.storage.AnimeManager;
import com.pypisan.kinani.storage.Constant;
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
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().
                    getPackageInfo(getApplicationContext().getPackageName(), 0);
            Constant.versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {}

            Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.userUrl)
                    .addConverterFactory(GsonConverterFactory.create()).build();
            RequestModule getID = retrofit.create(RequestModule.class);
            Call<UserModel> call = getID.getUser(new UserInit(deviceUser, origin,myVersion,Constant.versionName));
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
                        boolean terms = false;
                        boolean isAgree = false;
                        if (resource.getTerms() != null) {
                            terms = resource.getTerms();
                        }
                        if (!terms) {
                            showTerms();
                            }
                        else{
                            Intent intent = new Intent(HomeSplash.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            getAppAbout();
                        }
                    } else {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Failed, Try Again", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {finish();}}, 1000);
                        }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Server Down", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {finish();}}, 1000);
                }
            });
//        }


    }

    private void showTerms(){
        Dialog myDialog = new Dialog(HomeSplash.this);
        myDialog.setContentView(R.layout.privacy_dailog);
        myDialog.setCancelable(false);
        myDialog.show();
        Button agreeButton = myDialog.findViewById(R.id.agreeButton);
        Button disagreeButton = myDialog.findViewById(R.id.disagreeButton);
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termAgreed();
                Intent intent = new Intent(HomeSplash.this, MainActivity.class);
                startActivity(intent);
                finish();
                getAppAbout();
            }
        });

        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
                finish();
            }
        });
    }

    private void termAgreed(){
        UserUpdate userUpdate = new UserUpdate(Constant.uid, Constant.logo, Constant.loggedInStatus, false, true);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.userUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
        RequestModule updateUserData = retrofit.create(RequestModule.class);
        Call<UserModel> call = updateUserData.updateUser(Constant.key,userUpdate);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
            }
            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
            }
        });
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

    // check version and update
    private void askForUpdate(){
        String currentVersion = Constant.versionName;
//        new ForceUpdateAsync(currentVersion,BaseActivity.this).execute();
    }

    private void downloadUpdate(){

    }

}