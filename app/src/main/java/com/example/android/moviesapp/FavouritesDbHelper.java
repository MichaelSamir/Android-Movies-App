package com.example.android.moviesapp;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Michael on 4/23/2016.
 */
public class FavouritesDbHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = FavouritesDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private Context context;

    static final String DATABASE_NAME = "favourites.db";
    public FavouritesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITE_MOVIES_TABLE = "CREATE TABLE favourite_movies (movie_id INTEGER PRIMARY KEY,original_title TEXT NOT NULL,poster_path TEXT NOT NULL,overview TEXT NOT NULL,vote_average REAL NOT NULL,release_date TEXT NOT NULL)";
         try{
            Log.i(LOG_TAG, "SQL CREATE STATEMENT " + SQL_CREATE_FAVOURITE_MOVIES_TABLE);

            sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_MOVIES_TABLE);

        }
        catch(SQLException e){
            e.printStackTrace();
            Log.i(LOG_TAG, "creation field! ");

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        try{
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS favourite_movies");
            onCreate(sqLiteDatabase);
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }
}

