package com.pypisan.kinani.view;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.pypisan.kinani.R;
import com.pypisan.kinani.api.ApiRequest;
import com.pypisan.kinani.utils.SearchableActivity;

public class MainActivity extends AppCompatActivity {
    //    Bottom Nav Listener
    private final BottomNavigationView.OnItemSelectedListener navListner = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Fragment current = getSupportFragmentManager().findFragmentByTag("home_fragment");

            switch (item.getItemId()) {
                case R.id.newRelease:
                    selectedFragment = new RecentView();
                    break;
                case R.id.trending:
                    selectedFragment = new TrendingView();
                    break;
                case R.id.liked:
                    selectedFragment = new LikedView();
                    break;
                case R.id.home:
                    selectedFragment = new HomeView();
                    break;
            }
//            Begin Transition
            Log.d("fragHello", "1 " + selectedFragment + "2 " + current);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentView, selectedFragment)
                    .hide(current)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
    };
    private CastContext mCastContext;
    private BottomNavigationView bottomNav;
    private ApiRequest apiRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_KinAni);
        setContentView(R.layout.activity_main);

//      for populating recent, animeSchedule db
        apiRequest = new ApiRequest();
        apiRequest.recentFetcher(this);
//        apiRequest.scheduleFetcher(this);

        mCastContext = CastContext.getSharedInstance(this);

        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setBackground(null);
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
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient linearGradient = new LinearGradient(0, 0, width, height,
                        new int[]{
                                0xFF1e5799,
                                0xFF207cca,
                                0xFF2989d8,
                                0xFF207cca}, //substitute the correct colors for these
                        new float[]{
                                0, 0.40f, 0.60f, 1},
                        Shader.TileMode.REPEAT);
                return linearGradient;
            }
        };
        PaintDrawable paint = new PaintDrawable();
        paint.setShape(new RectShape());
        paint.setShaderFactory(shaderFactory);

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
                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                startActivity(intent);
                return true;
            case R.id.account:
                Toast.makeText(getApplicationContext(), "search Hi", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.media_route_menu_item:
                Toast.makeText(getApplicationContext(), "search Cast", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}