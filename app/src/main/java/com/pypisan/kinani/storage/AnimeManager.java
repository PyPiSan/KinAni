package com.pypisan.kinani.storage;

import static com.airbnb.lottie.L.TAG;
import static com.pypisan.kinani.storage.AnimeBase._ID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AnimeManager {

    private  AnimeBase animeBase;
    private Context context;
    private SQLiteDatabase database;

    public AnimeManager(Context c){
        context = c;
    }

    public AnimeManager open() throws SQLException{
        animeBase = new AnimeBase(context);
        database = animeBase.getWritableDatabase();
        return this;
    }

    public void close(){
        animeBase.close();
    }

    public void deleteLiked(String title){
        database = animeBase.getWritableDatabase();
        database.delete(AnimeBase.TABLE_NAME, "title=?", new String[]{title});
        database.close();
    }

//    For Liked View
    public Cursor readAllDataLiked(){
        String query = "SELECT * FROM " + AnimeBase.TABLE_NAME + " ORDER BY "+ _ID + " DESC";
        database = animeBase.getWritableDatabase();
        Cursor cursor = null;
        if (database != null){
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public void insertLiked(String detail, String title, String imageLink, String showType){
        Cursor cursor;
        cursor = database.rawQuery("SELECT title FROM AnimeLiked WHERE title=?", new String[]{title});
        if (cursor.getCount() == 0){ContentValues contentValues = new ContentValues();
            contentValues.put(AnimeBase.DETAIL, detail);
            contentValues.put(AnimeBase.TITLE, title);
            contentValues.put(AnimeBase.IMAGE, imageLink);
            contentValues.put(AnimeBase.TYPE, showType);
            database.insert(AnimeBase.TABLE_NAME, null, contentValues);}
    }

    public Cursor findOne(String title){
        Cursor cursor;
        cursor = database.rawQuery("SELECT title FROM AnimeLiked WHERE title=?", new String[]{title});
        return cursor;
    }
//    Recent View Table
    public void insertRecent(String detail, String title, String imageLink,String showType){
        Cursor cursor;
        cursor = database.rawQuery("SELECT title FROM AnimeRecent WHERE title=?", new String[]{title});
        if (cursor.getCount() >= 1){
            database.delete(AnimeBase.TABLE_NAME_2, "title=?", new String[]{title});
        }ContentValues contentValues = new ContentValues();
        contentValues.put(AnimeBase.DETAIL, detail);
        contentValues.put(AnimeBase.TITLE, title);
        contentValues.put(AnimeBase.IMAGE, imageLink);
        contentValues.put(AnimeBase.TYPE, showType);
        database.insert(AnimeBase.TABLE_NAME_2, null, contentValues);
    }

    public void insertContinueWatch(String detail, String title, String imageLink,String showType,
                                    String episode, Integer time){
        Cursor cursor;
        cursor = database.rawQuery("SELECT title FROM ContinueWatch WHERE title=?", new String[]{title});
        if (cursor.getCount() >= 1){
            database.delete(AnimeBase.TABLE_NAME_4, "title=?", new String[]{title});
        }ContentValues contentValues = new ContentValues();
        contentValues.put(AnimeBase.DETAIL, detail);
        contentValues.put(AnimeBase.TITLE, title);
        contentValues.put(AnimeBase.IMAGE, imageLink);
        contentValues.put(AnimeBase.TYPE, showType);
        contentValues.put(AnimeBase.EPISODE, episode);
        contentValues.put(AnimeBase.TIME, time);
        database.insert(AnimeBase.TABLE_NAME_4, null, contentValues);
    }

    public Cursor readAllDataRecent(){
        String query = "SELECT * FROM " + AnimeBase.TABLE_NAME_2 + " ORDER BY "+ _ID + " DESC";
        database = animeBase.getWritableDatabase();
        Cursor cursor = null;
        if (database != null){
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

//    Insert User credentials
    public void insertUser(String user, String apikey, Boolean ads, Boolean loggedIn, Integer logo){
    Cursor cursor;
//    long result = 0;
    cursor = database.rawQuery("SELECT user FROM UserData WHERE user=?", new String[]{user});
    if (cursor.getCount() == 0){
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnimeBase.USER, user);
        contentValues.put(AnimeBase.APIKEY, apikey);
        contentValues.put(AnimeBase.ADSTATUS, ads);
        contentValues.put(AnimeBase.LOGINSTATUS, loggedIn);
        contentValues.put(AnimeBase.LOGO, logo);
        database.insert(AnimeBase.TABLE_NAME_3, null, contentValues);
    }else if (cursor.getCount()==1){
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnimeBase.ADSTATUS, ads);
        contentValues.put(AnimeBase.LOGINSTATUS, loggedIn);
        contentValues.put(AnimeBase.LOGO, logo);
        database.update(AnimeBase.TABLE_NAME_3,contentValues,"user=?",new String[]{user});
        }
//    if (result==-1){
//        Log.d("Hi", "Not inserted");
//        }
    }

    public Cursor findOneUser(String user){
        Cursor cursor;
        cursor = database.rawQuery("SELECT * FROM UserData WHERE user=?", new String[]{user});
        return cursor;
    }

    public Cursor findAllUser(){
        Cursor cursor;
        cursor = database.rawQuery("SELECT * FROM UserData LIMIT 1",null);
        return cursor;
    }

//    For Continue Watching
    public Cursor readAllDataContinueWatch() {
        String query = "SELECT * FROM " + AnimeBase.TABLE_NAME_4 + " ORDER BY "+ _ID + " DESC";
        database = animeBase.getWritableDatabase();
        Cursor cursor = null;
        if (database != null){
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateMessage(String user, String message){
        Cursor cursor;
        cursor = database.rawQuery("SELECT * FROM UserData WHERE user=?",new String[]{user});
        if (cursor.getCount() == 1){
            ContentValues contentValues = new ContentValues();
            contentValues.put(AnimeBase.MESSAGE, message);
            database.update(AnimeBase.TABLE_NAME_3,contentValues,"user=?",new String[]{user});
        }
    }
}
