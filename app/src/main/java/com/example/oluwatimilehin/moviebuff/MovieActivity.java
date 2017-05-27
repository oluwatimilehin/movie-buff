package com.example.oluwatimilehin.moviebuff;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MovieActivity extends AppCompatActivity {


    final static int MOVIE_LOADER_ID = 3;
    ProgressBar mProgressBar;
    ArrayList<Movies> movies = new ArrayList<Movies>();
    private Toolbar toolbar;
    private TextView toolbarText;
    private MenuItem topRatedItem;
    private MenuItem popularItem;
    private Bundle bundle = new Bundle();
    private TextView mErrorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        topRatedItem = (MenuItem) findViewById(R.id.top_rated);
        popularItem = (MenuItem) findViewById(R.id.popular);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_indicator);
        mErrorTv = (TextView) findViewById(R.id.error_tv);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String s = "popular";
        bundle.putString("query", s);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle, new MovieDataLoader());

    }

    private void restartLoader(String s) {
        bundle.putString("query", s);
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, bundle, new MovieDataLoader());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        //Change the color of the icon
        Drawable drawable = menu.getItem(0).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.textColor, null), PorterDuff.Mode.SRC_ATOP);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String s = "query";
        switch (id) {
            case R.id.top_rated:
                if (toolbarText.getText() == getString(R.string.top_rated)) {
                    return true;
                }
                s = "top_rated";
                toolbarText.setText(getString(R.string.top_rated));
                restartLoader(s);
                break;
            case R.id.popular:
                if (toolbarText.getText() == getString(R.string.popular)) {
                    return true;
                }
                s = "popular";
                toolbarText.setText(getString(R.string.popular));
                restartLoader(s);
                break;
        }
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public class MovieDataLoader implements LoaderManager.LoaderCallbacks<ArrayList<Movies>> {

        MovieRVAdapter adapter;
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_movies);

        @Override
        public Loader<ArrayList<Movies>> onCreateLoader(int id, final Bundle args) {
            String apiKey = getString(R.string.api_key);

            ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                    .getSystemService(CONNECTIVITY_SERVICE);

            NetworkInfo info = cm.getActiveNetworkInfo();

            if (info != null) {
                if (info.isConnectedOrConnecting()) {
                    mErrorTv.setVisibility(View.INVISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.VISIBLE);
                }
                return new MovieLoader(MovieActivity.this, apiKey, args);
            }
            else {
                mErrorTv.setVisibility(View.VISIBLE);
                rv.setVisibility(View.INVISIBLE);
                mErrorTv.setText(getString(R.string.internet_error));
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movies>> loader, ArrayList<Movies> data) {
            movies = data;
            mProgressBar.setVisibility(View.GONE);
            adapter = new MovieRVAdapter(movies);
            rv.setLayoutManager(new GridLayoutManager(loader.getContext(), 2));
            rv.setAdapter(adapter);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movies>> loader) {
            movies.clear();
            loader.forceLoad();
        }


    }
}



