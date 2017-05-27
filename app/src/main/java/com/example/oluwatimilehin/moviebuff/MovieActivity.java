package com.example.oluwatimilehin.moviebuff;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MovieActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private Bundle bundle = new Bundle();
    final static int MOVIE_LOADER_ID = 3;
    private TextView mErrorTv;
    ProgressBar mProgressBar;
    ArrayList<Movies> movies = new ArrayList<Movies>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mProgressBar = (ProgressBar) findViewById(R.id.pb_indicator);
        mErrorTv = (TextView) findViewById(R.id.error_tv);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String s = "top_rated";
        bundle.putString("query", s);

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle,new MovieDataLoader());

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public class MovieDataLoader implements LoaderManager.LoaderCallbacks<ArrayList<Movies>> {
        @Override
        public Loader<ArrayList<Movies>> onCreateLoader(int id, final Bundle args) {
            String apiKey = getString(R.string.api_key);

            ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                    .getSystemService(CONNECTIVITY_SERVICE);

            NetworkInfo info = cm.getActiveNetworkInfo();

            if(info != null) {
                if(info.isConnectedOrConnecting()) {
                    mErrorTv.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    return new MovieLoader(MovieActivity.this, apiKey, args);
                }
            }
            mErrorTv.setVisibility(View.VISIBLE);
            mErrorTv.setText(getString(R.string.internet_error));
            return null;
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movies>> loader, ArrayList<Movies> data) {
            movies = data;
            mProgressBar.setVisibility(View.GONE);
            RecyclerView rv = (RecyclerView) findViewById(R.id.rv_movies);
            MovieRVAdapter adapter = new MovieRVAdapter(movies);
            rv.setLayoutManager(new GridLayoutManager(loader.getContext(), 2));
            rv.setAdapter(adapter);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movies>> loader) {

        }
    }
}



