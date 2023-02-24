package com.pypisan.kinani.view;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.pypisan.kinani.R;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.api.UserRequest;
import com.pypisan.kinani.model.UserModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private int HomeIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_KinAni);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomAppBar);
        bottomNav.setOnItemSelectedListener(navListner);

//        Toolbar setting
        Toolbar myToolbar = findViewById(R.id.topNav);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        Home Fragment as default
        Fragment fragment = HomeView.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentView, fragment, "home_fragment")
                .commit();

//        For Gradient Color
//        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
//            @Override
//            public Shader resize(int width, int height) {
//                LinearGradient linearGradient = new LinearGradient(0, 0, width, height,
//                        new int[]{
//                                0xFF1e5799,
//                                0xFF207cca,
//                                0xFF2989d8,
//                                0xFF207cca}, //substitute the correct colors for these
//                        new float[]{
//                                0, 0.40f, 0.60f, 1},
//                        Shader.TileMode.REPEAT);
//                return linearGradient;
//            }
//        };
//        PaintDrawable paint = new PaintDrawable();
//        paint.setShape(new RectShape());
//        paint.setShaderFactory(shaderFactory);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu,
                R.id.media_route_menu_item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                onSearchClicked();
                return true;
            case R.id.account:
                callLoginDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void callLoginDialog()
    {
        Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.user_dialog);
        myDialog.setCancelable(false);
        Button login = myDialog.findViewById(R.id.loginButton);
        Button cancel = myDialog.findViewById(R.id.cancelButton);
        EditText username = myDialog.findViewById(R.id.et_username);
        EditText password = myDialog.findViewById(R.id.et_password);
        myDialog.show();

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String user = String.valueOf(username.getText());
                String pass = String.valueOf(password.getText());

                String[] apiVal = new String[1];
                Retrofit retrofit = new Retrofit.Builder().baseUrl("https://anime.pypisan.com/v1/")
                        .addConverterFactory(GsonConverterFactory.create()).build();

                RequestModule getID = retrofit.create(RequestModule.class);
                Call<UserModel> call = getID.getUser(new UserRequest(user, pass));
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
                            apiVal[0] = resource.getApikey();
                            Toast.makeText(getApplicationContext(), apiVal[0], Toast.LENGTH_SHORT).show();
                            myDialog.cancel();
                        } else {
                            apiVal[0] = "";
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
            }
        });
    }

    private void onSearchClicked(){
        Fragment fragment = new SearchListView();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentView, fragment)
                .addToBackStack(null)
                .commit();
    }

    //    Bottom Nav Listener
    private final BottomNavigationView.OnItemSelectedListener navListner = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            String tag = null;
            Fragment currentFrag = getSupportFragmentManager().findFragmentByTag("home_fragment");

            switch (item.getItemId()) {
                case R.id.newRelease:
                    selectedFragment = new RecentView();
                    tag = "recent_fragment";
                    break;
                case R.id.movies:
                    selectedFragment = new MoviesView();
                    tag = "movies_fragment";
                    break;
                case R.id.liked:
                    selectedFragment = new LikedView();
                    tag = "liked_fragment";
                    break;
                case R.id.home:
                    selectedFragment = new HomeView();
                    tag = "home_fragment";
                    break;
            }
            int currentStack = getSupportFragmentManager().getBackStackEntryCount();
            if (tag.equals("home_fragment")){
                HomeIndex = getSupportFragmentManager().getBackStackEntryCount() +1 ;
            }
//            Begin Transition
//            Log.d("fragHello", "1 " + tag + " backstack " + currentStack + " homeindex " + HomeIndex);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentView, selectedFragment, tag)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
    };
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount()==HomeIndex ) {
            moveTaskToBack(true);
        } else {
            // if there is only one entry in the backstack, show the home screen
            getSupportFragmentManager().popBackStack();
        }
    }
}