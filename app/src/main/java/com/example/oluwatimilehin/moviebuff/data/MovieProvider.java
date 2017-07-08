package com.example.oluwatimilehin.moviebuff.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.oluwatimilehin.moviebuff.data.MovieContract.FavoritesEntry;

/**
 * Created by Oluwatimilehin on 05/07/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class MovieProvider extends ContentProvider {

    private static final int MOVIES = 261;
    private static final int MOVIE_ID = 781;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        String content = MovieContract.CONTENT_AUTHORITY;

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, MovieContract.PATH_FAVORITES, MOVIES );
        matcher.addURI(content, MovieContract.PATH_FAVORITES + "/#", MOVIE_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)){
            case MOVIES:
                retCursor = db.query(
                        FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        FavoritesEntry.TABLE_NAME,
                        projection,
                        FavoritesEntry._ID + "  = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case MOVIES:
                id = db.insert(FavoritesEntry.TABLE_NAME,null, values );
                if(id >= 0){
                    returnUri = FavoritesEntry.buildMovieUri(id);
                    Toast.makeText(getContext(), "Added to favorites.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Unable to add to favorites. Please try again",
                            Toast
                            .LENGTH_SHORT)
                            .show();
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);


        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numRows;

        switch (sUriMatcher.match(uri)){
            case MOVIES:
                numRows = db.delete(FavoritesEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if(selection == null || numRows != 0){
            getContext().getContentResolver().notifyChange(uri, null);

        }

        return numRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch (sUriMatcher.match(uri)){
            case MOVIES:
                rows = db.update(FavoritesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if(rows != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;

    }
}
