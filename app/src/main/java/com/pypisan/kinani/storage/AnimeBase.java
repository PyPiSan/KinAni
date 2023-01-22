package com.pypisan.kinani.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AnimeBase extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "AnimeLiked";
    public static final String _ID = "id";
    public static final String JTITLE = "jtitle";
    public static final String TITLE = "title";
    public static final String IMAGE = "imageLink";
    public static final String EPISODE = "episodeName";
    public static final String SCHEDULE = "scheduleDay";
    public static final String TABLE_NAME_2 = "AnimeRecent";
    public static final String TABLE_NAME_3 = "AnimeSched";

    static final String DB_NAME = "ANIME.DB";
    static final int DB_VERSION = 2;
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + JTITLE + " TEXT NOT NULL, " + TITLE +
            " TEXT NOT NULL, " + IMAGE + " TEXT);";

    private static final String CREATE_TABLE2 = "create table " + TABLE_NAME_2 + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + JTITLE + " TEXT NOT NULL, " + TITLE +
            " TEXT NOT NULL, " + IMAGE + " TEXT);";

    private static final String CREATE_TABLE3 = "create table " + TABLE_NAME_3 + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + JTITLE + " TEXT NOT NULL, " + TITLE +
            " TEXT NOT NULL, " + IMAGE + " TEXT NOT NULL, " + EPISODE + " TEXT NOT NULL," +
            SCHEDULE + " TEXT);";

    //    constructor
    public AnimeBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE3);
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_3);
        onCreate(db);
    }
}
