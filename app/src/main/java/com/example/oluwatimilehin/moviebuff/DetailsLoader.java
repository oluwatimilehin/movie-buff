package com.example.oluwatimilehin.moviebuff;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Oluwatimilehin on 03/07/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class DetailsLoader extends AsyncTaskLoader<Bundle> {

    String apiKey;
    int id;

    public DetailsLoader(Context context, String apiKey, int id) {
        super(context);
        this.apiKey = apiKey;
        this.id = id;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Bundle loadInBackground() {
        return NetworkUtils.parseJson(id, apiKey);
    }
}
