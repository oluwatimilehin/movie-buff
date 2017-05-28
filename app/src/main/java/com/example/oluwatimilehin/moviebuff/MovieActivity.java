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
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MovieActivity extends AppCompatActivity {

    final static int MOVIE_LOADER_ID = 3;
    NetworkInfo info;
    ProgressBar mProgressBar;
    ArrayList<Movies> movies = null;
    String query;
    private Toolbar toolbar;
    private TextView toolbarText;
    private Bundle bundle = new Bundle();
    private TextView mErrorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Prevents the
        // screen from being rotatable

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mProgressBar = (ProgressBar) findViewById(R.id.pb_indicator);
        mErrorTv = (TextView) findViewById(R.id.error_tv);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        query = "popular";
        bundle.putString("query", query);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle, new MovieDataLoader());

    }

    /**
     * This method is to generate a new random integer so that the loader has a unique ID.
     *
     * @param random
     * @return
     */
    private int anyRandomInt(Random random) {
        return random.nextInt();
    }


    private void restartLoader(String s) {
        Random random = new Random();
        int uniqueId = anyRandomInt(random); //Generates a new ID for each loader call;

        bundle.putString("query", s);

        if (getSupportLoaderManager().getLoader(MOVIE_LOADER_ID) != null) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, bundle, new
                    MovieDataLoader());
        } else {
            getSupportLoaderManager().initLoader(uniqueId, bundle, new MovieDataLoader());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        /**
         * Changes the color of the icons
         */
        Drawable drawable = menu.getItem(0).getIcon();
        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (drawable != null && overflowIcon != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.textColor, null), PorterDuff.Mode.SRC_ATOP);

            overflowIcon.mutate();
            overflowIcon.setColorFilter(getResources().getColor(R.color.textColor, null), PorterDuff.Mode
                    .SRC_ATOP);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.top_rated:
                if (toolbarText.getText() == getString(R.string.top_rated)) {
                    return true;
                }
                item.setChecked(true);
                query = "top_rated";
                toolbarText.setText(getString(R.string.top_rated));
                restartLoader(query);
                break;
            case R.id.popular:
                if (toolbarText.getText() == getString(R.string.popular)) {
                    return true;
                }
                item.setChecked(true);
                query = "popular";
                toolbarText.setText(getString(R.string.popular));
                restartLoader(query);
                break;
            case R.id.action_refresh:
                if (toolbarText.getText() == getString(R.string.popular)) {
                    query = "popular";
                } else {
                    query = "top_rated";
                }
                movies = null;
                restartLoader(query);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method checks for the internet connection status
     *
     * @return
     */
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(CONNECTIVITY_SERVICE);

        info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    /**
     * Method used to install the Calligraphy library
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public class MovieDataLoader implements LoaderManager.LoaderCallbacks<ArrayList<Movies>> {

        MovieRVAdapter adapter = null;
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_movies);

        private void showErrorScreen(){
            mErrorTv.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            rv.setVisibility(View.INVISIBLE);
            mErrorTv.setText(getString(R.string.internet_error));
        }

        @Override
        public Loader<ArrayList<Movies>> onCreateLoader(int id, final Bundle args) {
            String apiKey = getString(R.string.api_key);

            boolean isConnected = isConnected();

            if (isConnected) {
                mErrorTv.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                rv.setVisibility(View.VISIBLE);

                return new MovieLoader(MovieActivity.this, apiKey, args);
            }
            showErrorScreen();
            return null;
        }


        @Override
        public void onLoadFinished(Loader<ArrayList<Movies>> loader, ArrayList<Movies> data) {
            mProgressBar.setVisibility(View.GONE);
            if (data != null) {
                if (movies != null) {
                    movies.clear();
                    movies.addAll(data);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    movies = data;
                }
                adapter = new MovieRVAdapter(movies);

                rv.setLayoutManager(new GridLayoutManager(loader.getContext(), 2));
                rv.setAdapter(adapter);
            } else {
                showErrorScreen();
            }


        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movies>> loader) {
            //super(loader);
        }

    }
}



