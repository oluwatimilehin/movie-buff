package com.example.oluwatimilehin.moviebuff.details;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.example.oluwatimilehin.moviebuff.network.NetworkUtils;

/**
 * Created by Oluwatimilehin on 03/07/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class DetailsLoader extends AsyncTaskLoader<Bundle> {

    String apiKey;
    Bundle mBundle;

    public DetailsLoader(Context context, String apiKey, Bundle bundle) {
        super(context);
        this.apiKey = apiKey;
        mBundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Bundle loadInBackground() {

        int id = mBundle.getInt("id");
        return NetworkUtils.parseJson(id, apiKey);
    }
}
