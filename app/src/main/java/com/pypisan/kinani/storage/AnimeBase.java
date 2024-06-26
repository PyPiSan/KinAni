package com.pypisan.kinani.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AnimeBase extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "AnimeLiked";
    public static final String _ID = "id";
    public static final String DETAIL = "detail";
    public static final String TITLE = "title";
    public static final String IMAGE = "imageLink";
    public static final String TYPE = "showType";
    public static final String TABLE_NAME_2 = "AnimeRecent";

    public static final String TABLE_NAME_3 = "UserData";
    public static final String USER = "user";
    public static final String APIKEY = "apikey";
    public static final String ADSTATUS = "ad";
    public static final String LOGINSTATUS = "logged";
    public static final String MESSAGE = "message";
    public static final String LOGO = "logo";
    public static final String TABLE_NAME_4 = "ContinueWatch";
    public static final String EPISODE = "episode";
    public static final String TIME = "time";
    static final String DB_NAME = "ANIME.DB";
    static final int DB_VERSION = 20;
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DETAIL + " TEXT NOT NULL, " + TITLE +
            " TEXT NOT NULL, " + IMAGE + " TEXT, " + TYPE + " TEXT);";

    private static final String CREATE_TABLE2 = "create table " + TABLE_NAME_2 + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DETAIL + " TEXT NOT NULL, " + TITLE +
            " TEXT NOT NULL, " + IMAGE + " TEXT, " + TYPE + " TEXT);";

    private static final String CREATE_TABLE3 = "create table " + TABLE_NAME_3 + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER + " TEXT NOT NULL, " + APIKEY +
            " TEXT NOT NULL, " + ADSTATUS + " BOLLEAN NOT NULL, " + LOGINSTATUS
            + " BOLLEAN NOT NULL, " + LOGO + " INTEGER, " + MESSAGE + " TEXT);";

    private static final String CREATE_TABLE4 = "create table " + TABLE_NAME_4 + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DETAIL + " TEXT NOT NULL, " + TITLE +
            " TEXT NOT NULL, " + IMAGE + " TEXT, " + TYPE + " TEXT, " + EPISODE + " TEXT, " +
        TIME + " INTEGER);";

    //    constructor
    public AnimeBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE2);
        db.execSQL(CREATE_TABLE3);
        db.execSQL(CREATE_TABLE4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_4);
        onCreate(db);
    }
}
