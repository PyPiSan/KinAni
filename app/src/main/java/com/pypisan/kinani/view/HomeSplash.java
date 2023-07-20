package com.pypisan.kinani.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.inmobi.sdk.InMobiSdk;
import com.inmobi.sdk.SdkInitializationListener;
import com.pypisan.kinani.R;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.api.UserRequest;
import com.pypisan.kinani.model.UserModel;
import com.pypisan.kinani.storage.AnimeManager;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeSplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_splash);
        AnimeManager animeManager = new AnimeManager(getApplicationContext());
        JSONObject consentObject = new JSONObject();
        try {
            // Provide correct consent value to sdk which is obtained by User
            consentObject.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, true);
            // Provide 0 if GDPR is not applicable and 1 if applicable
            consentObject.put("gdpr", "0");
            // Provide user consent in IAB format
            consentObject.put(InMobiSdk.IM_GDPR_CONSENT_IAB, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        InMobiSdk.init(this, "b048bb9ebf634fb78efeb09f053ac225", consentObject, new SdkInitializationListener() {
            @Override
            public void onInitializationComplete(@Nullable Error error) {
                if (null != error) {
                    Log.e("InMobi", "InMobi Init failed -" + error.getMessage());
                } else {
                    Log.d("InMobi", "InMobi Init Successful");
                }
            }
        });


        @SuppressLint("HardwareIds") String deviceUser = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        animeManager.open();
        Cursor cursor = animeManager.findOneUser(deviceUser);
        if (cursor != null && cursor.getCount() != 0){
            animeManager.close();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeSplash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);

        }else {
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://anime.pypisan.com/v1/")
                    .addConverterFactory(GsonConverterFactory.create()).build();
            RequestModule getID = retrofit.create(RequestModule.class);
            Call<UserModel> call = getID.getUser(new UserRequest(deviceUser, "xYz1254tvebej"));
            call.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                    boolean flag = false;
                    UserModel resource = response.body();
                    if (response.code() == 200) {
                        boolean status = resource.getUserStatus();
                        flag = status;
                    }
                    if (flag) {
                        animeManager.insertUser(deviceUser, resource.getApikey());
                        Toast.makeText(getApplicationContext(), resource.getApikey(), Toast.LENGTH_SHORT).show();
                        animeManager.close();
                        Intent intent = new Intent(HomeSplash.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Failed Server", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}