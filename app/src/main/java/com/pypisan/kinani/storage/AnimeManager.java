package com.pypisan.kinani.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
        contentValues.put(AnimeBase.JTITLE, jtitle);
        contentValues.put(AnimeBase.TITLE, title);
        contentValues.put(AnimeBase.IMAGE, animeLink);
        database.insert(AnimeBase.TABLE_NAME, null, contentValues);
    }

    public Cursor fetch(){
        String[] columns = new String[] {AnimeBase._ID, AnimeBase.JTITLE, AnimeBase.TITLE,
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
    public void delete(String jtitle){
        database = animeBase.getWritableDatabase();
        database.delete(AnimeBase.TABLE_NAME, "jtitle=?", new String[]{jtitle});;
        database.close();
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + AnimeBase.TABLE_NAME;
        database = animeBase.getWritableDatabase();
        Cursor cursor = null;
        if (database != null){
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

//    Recent View Table
    public void insertRecent(String jtitle, String title, String animeLink){
        ContentValues contentValues = new ContentValues();
        contentValues.put(AnimeBase.JTITLE, jtitle);
        contentValues.put(AnimeBase.TITLE, title);
        contentValues.put(AnimeBase.IMAGE, animeLink);
        database.insert(AnimeBase.TABLE_NAME_2, null, contentValues);
    }

    public Cursor readAllDataRecent(){
        String query = "SELECT * FROM " + AnimeBase.TABLE_NAME_2;
        database = animeBase.getWritableDatabase();
        Cursor cursor = null;
        if (database != null){
            cursor = database.rawQuery(query, null);
        }
        return cursor;
    }

//    Schedule List
    public void insertScheduleList(String jName, String title, String imageLink, String episode,
                                   String schedule){
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(AnimeBase.JTITLE, jName);
        contentValues2.put(AnimeBase.TITLE, title);
        contentValues2.put(AnimeBase.IMAGE, imageLink);
        contentValues2.put(AnimeBase.EPISODE, episode);
        contentValues2.put(AnimeBase.SCHEDULE, schedule);
        database.insert(AnimeBase.TABLE_NAME_3, null, contentValues2);
    }

    public  Cursor readAllScheduleData(){
        String query = "SELECT * FROM " + AnimeBase.TABLE_NAME_3;
        database = animeBase.getWritableDatabase();
        Cursor cursor = null;
        if (database != null){
            cursor = database.rawQuery(query, null);
        }
        return cursor;

    }
}
