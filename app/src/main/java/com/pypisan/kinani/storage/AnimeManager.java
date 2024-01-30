package com.pypisan.kinani.storage;

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

    public void insert(String jtitle, String title, String animeLink){
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnimeBase.DETAIL, jtitle);
        contentValues.put(AnimeBase.TITLE, title);
        contentValues.put(AnimeBase.IMAGE, animeLink);
        database.insert(AnimeBase.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch(){
        String[] columns = new String[] {_ID, AnimeBase.DETAIL, AnimeBase.TITLE,
        AnimeBase.IMAGE};

        Cursor cursor = database.query(AnimeBase.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null);

        if (cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
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
    public void insertUser(String user, String apikey){
    Cursor cursor;
    cursor = database.rawQuery("SELECT user FROM UserData WHERE user=?", new String[]{user});
    if (cursor.getCount() == 0){ContentValues contentValues = new ContentValues();
        contentValues.put(AnimeBase.USER, user);
        contentValues.put(AnimeBase.APIKEY, apikey);
        database.insert(AnimeBase.TABLE_NAME_3, null, contentValues);}
        }

    public Cursor findOneUser(String user){
        Cursor cursor;
        cursor = database.rawQuery("SELECT * FROM UserData WHERE user=?", new String[]{user});
        return cursor;
    }
}
