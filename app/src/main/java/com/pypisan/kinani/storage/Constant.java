package com.pypisan.kinani.storage;

import android.os.Environment;

import com.pypisan.kinani.R;

public class Constant {
    public static String key;
    public static String uid;
    public static final String baseUrl="https://anime.pypisan.com/v1/anime/";
    public static final String baseDramaUrl="https://drama.pypisan.com/v1/drama/";
    public static final String userUrl ="https://anime.pypisan.com/v1/";
    public static String privacyUrl ="https://pypisan.com/privacy";
    public static Integer logo;
    public static Boolean loggedInStatus=false;
    public static Boolean isFree=true;
    public static Boolean isMessage=false;
    public static String userName = "Anonymous";
    public static String versionName = "1.0.0";
    public static String about;
    public static String message;

    public static String  storageLocation = Environment.DIRECTORY_DOWNLOADS;

    public static final Integer[] userIconImage = {R.drawable.user_icon1,R.drawable.user_icon2,
            R.drawable.user_icon3, R.drawable.user_icon4,R.drawable.user_icon5,R.drawable.user_icon6,
            R.drawable.user_icon7,R.drawable.user_icon8,R.drawable.user_icon9,R.drawable.user_icon10,
            R.drawable.user_icon11,R.drawable.user_icon12,R.drawable.user_icon13,R.drawable.user_icon14,
            R.drawable.user_icon15,R.drawable.user_icon16,R.drawable.user_icon17,R.drawable.user_icon18,
            R.drawable.user_icon19};
}
