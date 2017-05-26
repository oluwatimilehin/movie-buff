package com.example.oluwatimilehin.moviebuff;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private Bundle bundle = new Bundle();
    final static int MOVIE_LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String s = "top_rated";
        bundle.putString("query", s);

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle,new MovieDataLoader());

    }

    public class MovieDataLoader implements LoaderManager.LoaderCallbacks<ArrayList<Movies>> {
        @Override
        public Loader<ArrayList<Movies>> onCreateLoader(int id, final Bundle args) {
            String apiKey = getString(R.string.api_key);
            return new MovieLoader(MovieActivity.this, apiKey, args);

        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movies>> loader, ArrayList<Movies> data) {


        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movies>> loader) {

        }
    }
}



