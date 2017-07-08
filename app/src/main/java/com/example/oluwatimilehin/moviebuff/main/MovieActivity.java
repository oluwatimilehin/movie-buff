package com.example.oluwatimilehin.moviebuff.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.oluwatimilehin.moviebuff.MasterActivity;
import com.example.oluwatimilehin.moviebuff.R;
import com.example.oluwatimilehin.moviebuff.details.DetailActivity;

import java.util.ArrayList;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MovieActivity extends MasterActivity {

    final static int MOVIE_LOADER_ID = 3;

    ProgressBar mProgressBar;
    ArrayList<Movies> movies = null;
    String query;
    private Toolbar toolbar;
    private TextView toolbarText;
    private Bundle bundle = new Bundle();
    private TextView mErrorTv;
    RecyclerView rv ;
    long currentVisiblePosition = 0;
    AppBarLayout mAppBarLayout;

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

        rv = (RecyclerView) findViewById(R.id.rv_movies);
        rv.setNestedScrollingEnabled(false);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        query = "popular";
        bundle.putString("query", query);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle, new MovieDataLoader());

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(rv.getLayoutManager() != null) {
            currentVisiblePosition =((GridLayoutManager) rv.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
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
            drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.textColor ),
                    PorterDuff
                    .Mode.SRC_ATOP);

            overflowIcon.mutate();
            overflowIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.textColor),
                    PorterDuff.Mode
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



    @Override
    protected void onResume() {
        super.onResume();

        if(rv.getLayoutManager() != null) {
            rv.smoothScrollToPosition((int) currentVisiblePosition);
        }
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


        @Override
        public Loader<ArrayList<Movies>> onCreateLoader(int id, final Bundle args) {
            String apiKey = getString(R.string.api_key);
            boolean isConnected = isConnected(getApplicationContext());

            if (isConnected) {
                mErrorTv.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                rv.setVisibility(View.VISIBLE);

                return new MovieLoader(MovieActivity.this, apiKey, args);
            }

            showErrorScreen(mErrorTv, mProgressBar);
            rv.setVisibility(View.INVISIBLE);
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
                showErrorScreen(mErrorTv, mProgressBar);
                rv.setVisibility(View.INVISIBLE);
            }
            if(adapter != null) {
                adapter.setOnItemClickListener(new MovieRVAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        Movies currentMovie = movies.get(position);
                        String title = currentMovie.getTitle();
                        String imageUrl = currentMovie.getImageUrl();
                        String rating = currentMovie.getRating();
                        String plot = currentMovie.getPlot();
                        String releaseDate = currentMovie.getReleaseDate();
                        int id = currentMovie.getId();

                        Intent callingIntent = new Intent(MovieActivity.this, DetailActivity.class);

                        callingIntent.putExtra("title", title);
                        callingIntent.putExtra("imageUrl", imageUrl);
                        callingIntent.putExtra("rating", rating);
                        callingIntent.putExtra("plot", plot);
                        callingIntent.putExtra("releaseDate", releaseDate);
                        callingIntent.putExtra("id", id);

                        startActivity(callingIntent);
                    }
                });
            }
        }



        @Override
        public void onLoaderReset(Loader<ArrayList<Movies>> loader) {
            //super(loader);
        }

    }
}



