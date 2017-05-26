package com.example.oluwatimilehin.moviebuff;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Oluwatimilehin on 26/05/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class MovieLoader extends AsyncTaskLoader<ArrayList<Movies>>{
    Bundle mBundle = new Bundle();
    String mApiKey = null;

    public MovieLoader(Context context, String apiKey, Bundle args){
        super(context);
        mApiKey = apiKey;
        mBundle = args;

    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Movies> loadInBackground() {
        String apiKey = mApiKey;
        ArrayList<Movies> movies = NetworkUtils.parseJson(mBundle.getString("query"), apiKey);
        return movies;
    }
}
