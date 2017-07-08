package com.example.oluwatimilehin.moviebuff.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.oluwatimilehin.moviebuff.data.MovieContract.FavoritesEntry;

/**
 * Created by Oluwatimilehin on 05/07/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        addMovieTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteMovieTable(db);
        onCreate(db);
    }

    private void addMovieTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + FavoritesEntry.TABLE_NAME + "(" + FavoritesEntry._ID + " " +
                "INTEGER PRIMARY KEY, " + FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_RATING + " TEXT NOT NULL, " + FavoritesEntry
                .COLUMN_RELEASE_DATE + " TEXT NOT NULL, " + FavoritesEntry.COLUMN_SYNPOSIS + " " +
                "TEXT NOT NULL, " + FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                FavoritesEntry.COLUMN_YOUTUBE_LINK + " TEXT NOT NULL, " + FavoritesEntry
                .COLUMN_REVIEW + " TEXT, " + FavoritesEntry.COLUMN_IMAGE + " BLOB)" );
    }

    private void deleteMovieTable(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
    }
}
