package com.example.oluwatimilehin.moviebuff.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Oluwatimilehin on 05/07/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.oluwatimilehin.moviebuff";
    public static final String PATH_FAVORITES = "favorites";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath
                (PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_SYNPOSIS = "synposis";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_YOUTUBE_LINK = "link";
        public static final String COLUMN_IMAGE = "image";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
