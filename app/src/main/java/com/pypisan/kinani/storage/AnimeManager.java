package com.pypisan.kinani.storage;

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
        String[] columns = new String[] {AnimeBase._ID, AnimeBase.DETAIL, AnimeBase.TITLE,
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
    public void delete(String title){
        database = animeBase.getWritableDatabase();
        database.delete(AnimeBase.TABLE_NAME, "title=?", new String[]{title});;
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
    public void insertRecent(String detail, String title, String imageLink){
        Cursor cursor = null;
        cursor = database.rawQuery("SELECT title FROM AnimeRecent WHERE title=?", new String[]{title});
        Log.d("Hey1", "Cursor is : " + cursor.getCount());
        if (cursor.getCount() == 0){ContentValues contentValues = new ContentValues();
        contentValues.put(AnimeBase.DETAIL, detail);
        contentValues.put(AnimeBase.TITLE, title);
        contentValues.put(AnimeBase.IMAGE, imageLink);
        database.insert(AnimeBase.TABLE_NAME_2, null, contentValues);}
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

}
