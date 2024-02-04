package com.pypisan.kinani.view;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.pypisan.kinani.R;
import com.pypisan.kinani.api.RequestModule;
import com.pypisan.kinani.api.UserRequest;
import com.pypisan.kinani.api.SignUpRequest;
import com.pypisan.kinani.model.SignUpModel;
import com.pypisan.kinani.model.UserModel;
import com.pypisan.kinani.storage.AnimeManager;
import com.pypisan.kinani.storage.Constant;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private int HomeIndex;
    private Menu mMenuItem;
    private String uid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_KinAni);
        setContentView(R.layout.activity_main);
//
        BottomNavigationView bottomNav = findViewById(R.id.bottomAppBar);
//        newView = findViewById(R.id.newRelease);
//        movieView = findViewById(R.id.movies);
//        dramaView = findViewById(R.id.drama);
//        defaultView = findViewById(R.id.home);
        bottomNav.setOnItemSelectedListener(navListner);

//      Toolbar setting
        Toolbar myToolbar = findViewById(R.id.topNav);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//      Home Fragment as default
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
        mMenuItem=menu;
        if (Constant.loggedInStatus){
            menu.getItem(1).setIcon(Constant.logo);
        }
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
                if (!Constant.loggedInStatus){
                    callLoginDialog();
                }else{
                    openUserPage();
                }
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
        LinearLayout loginBox = myDialog.findViewById(R.id.loginBox);
        LinearLayout signUpBox = myDialog.findViewById(R.id.signUpBox);
        Button login = myDialog.findViewById(R.id.loginButton);
        TextView signUp = myDialog.findViewById(R.id.lbl_signup);
        TextView forgotPass = myDialog.findViewById(R.id.lbl_forgot);
        Button cancel = myDialog.findViewById(R.id.cancelButton);
        Button cancel2 = myDialog.findViewById(R.id.cancelButton2);
        Button hopIn = myDialog.findViewById(R.id.signUpButton);
//      Login Fields
        EditText username = myDialog.findViewById(R.id.et_username);
        EditText password = myDialog.findViewById(R.id.et_password);
        ProgressBar loginLoader = myDialog.findViewById(R.id.loginLoader);

//      Signup Fields
        EditText alias = myDialog.findViewById(R.id.et_name);
        EditText pass1 = myDialog.findViewById(R.id.et_new_password);
        EditText pass2 = myDialog.findViewById(R.id.et_repeat_password);
        EditText age = myDialog.findViewById(R.id.et_age);
        RadioGroup radioGroup = myDialog.findViewById(R.id.gender);
        myDialog.show();

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                String user = String.valueOf(username.getText());
                String pass = String.valueOf(password.getText());
                if (user.equals("") || pass.equals("")){
                    Toast.makeText(getApplicationContext(), "User and Password required", Toast.LENGTH_SHORT).show();
                }else{
                loginBox.setVisibility(View.GONE);
                loginLoader.setVisibility(View.VISIBLE);
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.userUrl)
                        .addConverterFactory(GsonConverterFactory.create()).build();

                RequestModule getID = retrofit.create(RequestModule.class);
                Call<UserModel> call = getID.getLogin(new UserRequest(user, pass));
                call.enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        boolean flag = false;
                        UserModel resource = response.body();
                        if (response.code() == 200) {
                            flag = resource.getUserStatus();
                        }
                        if (flag) {
                            Constant.loggedInStatus = true;
                            Constant.logo = resource.getIcon();
                            if (Constant.logo == null){
                                Constant.logo = randomUserIcon();
                            }
                            Constant.userName = user;
                            AnimeManager animeManager = new AnimeManager(getApplicationContext());
                            animeManager.open();
                            animeManager.insertUser(uid,"",true,true,Constant.logo);
                            animeManager.close();
                            mMenuItem.getItem(1).setIcon(Constant.logo);
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            myDialog.cancel();
                        } else {
                            Toast.makeText(getApplicationContext(), "Login Failed, "+resource.getMessage(), Toast.LENGTH_SHORT).show();
                            loginLoader.setVisibility(View.GONE);
                            loginBox.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Try Again", Toast.LENGTH_SHORT).show();
                        loginLoader.setVisibility(View.GONE);
                        loginBox.setVisibility(View.VISIBLE);
                    }
                });
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
            }
        });
        cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBox.setVisibility(View.GONE);
                signUpBox.setVisibility(View.VISIBLE);
            }
        });

        hopIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                String userValue = String.valueOf(alias.getText());
                String passValue = String.valueOf(pass1.getText());
                String repeatPass = String.valueOf(pass2.getText());
                String ageValue = String.valueOf(age.getText());
                String gender = "Other";
                if (userValue.equals("") || userValue.length()<8){
                    Toast.makeText(getApplicationContext(), "Alias must be greater than 8 chars",
                            Toast.LENGTH_LONG).show();
                }else if (passValue.equals("") || passValue.length()<8){
                    Toast.makeText(getApplicationContext(), "Password must be greater than 8 chars",
                            Toast.LENGTH_LONG).show();
                } else if (repeatPass.equals("")|| !passValue.equals(repeatPass)){
                    Toast.makeText(getApplicationContext(), "Password is not matching",
                            Toast.LENGTH_LONG).show();
                }else if (ageValue.equals("")){
                    Toast.makeText(getApplicationContext(), "Age must be a value",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    // get selected radio button from radioGroup
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    if (selectedId!=-1){
                        RadioButton radioButton = myDialog.findViewById(selectedId);
                        gender = radioButton.getText().toString();
                    }
                    signUpBox.setVisibility(View.GONE);
                    loginLoader.setVisibility(View.VISIBLE);
                    AnimeManager animeManager = new AnimeManager(getApplicationContext());
                    animeManager.open();
                    Cursor cursor = animeManager.findAllUser();
                    if (cursor != null && cursor.getCount() != 0){
                        while (cursor.moveToNext()) {
                            uid =cursor.getString(1);
                        }
                    }
                    animeManager.close();
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.userUrl)
                            .addConverterFactory(GsonConverterFactory.create()).build();
                    Constant.logo = randomUserIcon();
                    RequestModule userSignUp = retrofit.create(RequestModule.class);
                    Call<SignUpModel> call = userSignUp.signUp(Constant.key,
                            new SignUpRequest(uid,userValue, passValue,ageValue,gender, Constant.logo));

                    call.enqueue(new Callback<SignUpModel>() {
                        @Override
                        public void onResponse(Call<SignUpModel> call, Response<SignUpModel> response) {
                            boolean flag = false;
                            SignUpModel resource = response.body();
                            if (response.code() == 200) {
                                flag = resource.getStatus();
                            }
                            Toast.makeText(getApplicationContext(), resource.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            if (flag) {
                                Constant.loggedInStatus = true;
                                Constant.userName =userValue;
                                animeManager.open();
                                animeManager.insertUser(uid,"",true,true,Constant.logo);
                                animeManager.close();
                                mMenuItem.getItem(1).setIcon(Constant.logo);
                                myDialog.cancel();
                            } else {
                                loginLoader.setVisibility(View.GONE);
                                signUpBox.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(Call<SignUpModel> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Try Again ", Toast.LENGTH_LONG).show();
                            loginLoader.setVisibility(View.GONE);
                            signUpBox.setVisibility(View.VISIBLE);
                        }
                    });

                }


            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loginBox.setVisibility(View.GONE);
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

//  Bottom Nav Listener
    private final BottomNavigationView.OnItemSelectedListener navListner = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            String tag = null;
//          Fragment currentFrag = getSupportFragmentManager().findFragmentByTag("home_fragment");
            switch (item.getItemId()) {
                case R.id.newRelease:
                    selectedFragment = new RecentView();
                    tag = "recent_fragment";
                    break;
                case R.id.movies:
                    selectedFragment = new MoviesView();
                    tag = "movies_fragment";
                    break;
                case R.id.drama:
                    selectedFragment = new KShowView();
                    tag = "drama_fragment";
                    break;
                case R.id.home:
                    selectedFragment = new HomeView();
                    tag = "home_fragment";
                    break;
            }
            if (tag.equals("home_fragment")){
                HomeIndex = getSupportFragmentManager().getBackStackEntryCount() +1 ;
            }
//          Begin Transition
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentView, selectedFragment, tag)
                    .addToBackStack(null)
                    .commit();
//            Toast.makeText(getApplicationContext(),"View Selected is"+ Arrays.toString(movieView.getDrawableState()), Toast.LENGTH_LONG).show();
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

    private int randomUserIcon(){
        Random rand = new Random();
        Integer[] image = {R.drawable.user_icon3,
                R.drawable.user_icon4,R.drawable.user_icon5,R.drawable.user_icon6,
                R.drawable.user_icon7,R.drawable.user_icon8,R.drawable.user_icon9,R.drawable.user_icon10,
                R.drawable.user_icon11,R.drawable.user_icon12,R.drawable.user_icon13,R.drawable.user_icon14,
                R.drawable.user_icon15,R.drawable.user_icon16,R.drawable.user_icon17,R.drawable.user_icon18,
                R.drawable.user_icon19};
        int rand_int = rand.nextInt(image.length);
        return image[rand_int];
    }

    private void openUserPage(){
        Fragment fragment = new UserPageView();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentView, fragment)
                .addToBackStack(null)
                .commit();
    }
}