package com.example.oluwatimilehin.moviebuff.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.oluwatimilehin.moviebuff.Constants;
import com.example.oluwatimilehin.moviebuff.favorites.FavoritesAdapter;
import com.example.oluwatimilehin.moviebuff.MasterActivity;
import com.example.oluwatimilehin.moviebuff.R;
import com.example.oluwatimilehin.moviebuff.data.MovieContract.FavoritesEntry;
import com.example.oluwatimilehin.moviebuff.details.DetailActivity;
import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;

public class MovieActivity extends MasterActivity {

    private static final int FAVORITES_LOADER_ID = 258;
    static int MOVIE_LOADER_ID = 3;
    ProgressBar mProgressBar;
    ArrayList<Movies> movies = null;
    String query;
    FavoritesAdapter favoritesAdapter;
    RecyclerView moviesRv;
    long currentVisiblePosition = 0;
    AppBarLayout mAppBarLayout;
    LinearLayout layout;
    MovieRVAdapter mMovieRVAdapter = null;
    private Toolbar toolbar;
    private TextView toolbarText;
    private Bundle bundle = new Bundle();
    private TextView mErrorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Stetho.newInitializerBuilder(this)
                .enableDumpapp(
                        Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                        Stetho.defaultInspectorModulesProvider(this))
                .build();

        Stetho.initializeWithDefaults(this);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_indicator);
        mErrorTv = (TextView) findViewById(R.id.error_tv);

        layout = (LinearLayout) findViewById(R.id.main_layout);

        moviesRv = (RecyclerView) findViewById(R.id.rv_movies);
        moviesRv.setNestedScrollingEnabled(false);


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
        if (moviesRv.getLayoutManager() != null) {
            if (moviesRv.getLayoutManager() instanceof GridLayoutManager) {
                currentVisiblePosition = ((GridLayoutManager) moviesRv.getLayoutManager())
                        .findFirstVisibleItemPosition();
            }
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
        MOVIE_LOADER_ID = anyRandomInt(random); //Generates a new ID for each loader call;
        bundle.putString("query", s);

        if (getSupportLoaderManager().getLoader(MOVIE_LOADER_ID) != null) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, bundle, new
                    MovieDataLoader());
        } else {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle, new MovieDataLoader());
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
            drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.textColor),
                    PorterDuff
                            .Mode.SRC_ATOP);

            overflowIcon.mutate();
            overflowIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.textColor),
                    PorterDuff.Mode
                            .SRC_ATOP);
        }

        //Hides the option to refresh when on favorites screen
        if(toolbarText.getText().equals(getString(R.string.favorites))){
            menu.findItem(R.id.action_refresh).setVisible(false);
            menu.findItem(R.id.favorites).setChecked(true);
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
            case R.id.favorites:
                if (toolbarText.getText().equals(getString(R.string.favorites))) {
                    return true;
                }
                invalidateOptionsMenu();
                item.setChecked(true);
                toolbarText.setText(R.string.favorites);
                getSupportLoaderManager().destroyLoader(MOVIE_LOADER_ID);
                startFavoritesLoader();
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

        //Scroll to the previous position when activity is resumed
        if (moviesRv.getLayoutManager() != null) {
            moviesRv.scrollToPosition((int) currentVisiblePosition);
        }

        moviesRv.invalidate();

    }

    private void startFavoritesLoader() {
        if (getSupportLoaderManager().getLoader(FAVORITES_LOADER_ID) != null) {
            getSupportLoaderManager().restartLoader(FAVORITES_LOADER_ID, new Bundle(),
                    new FavoritesLoader());
            return;
        }
        getSupportLoaderManager().initLoader(FAVORITES_LOADER_ID, new Bundle(), new
                FavoritesLoader());

    }

    /**
     * Method used to install the Calligraphy library
     *
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public class MovieDataLoader implements LoaderManager.LoaderCallbacks<ArrayList<Movies>> {


        @Override
        public Loader<ArrayList<Movies>> onCreateLoader(int id, final Bundle args) {
            String apiKey = getString(R.string.api_key);
            boolean isConnected = isConnected(getApplicationContext());

            if (isConnected) {
                mErrorTv.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                moviesRv.setVisibility(View.VISIBLE);

                return new MovieLoader(MovieActivity.this, apiKey, args);
            }

            showErrorScreen(mErrorTv, mProgressBar);
            moviesRv.setVisibility(View.INVISIBLE);
            return null;
        }


        @Override
        public void onLoadFinished(Loader<ArrayList<Movies>> loader, ArrayList<Movies> data) {
            mProgressBar.setVisibility(GONE);
            if (data != null) {
                if (movies != null) {
                    movies.clear();
                    movies.addAll(data);
                    if (mMovieRVAdapter != null) {
                        mMovieRVAdapter.notifyDataSetChanged();
                    }
                } else {
                    movies = data;
                }
                mMovieRVAdapter = new MovieRVAdapter(movies);

                moviesRv.setLayoutManager(new GridLayoutManager(loader.getContext(), 2));
                moviesRv.setAdapter(mMovieRVAdapter);
            } else {
                showErrorScreen(mErrorTv, mProgressBar);
                moviesRv.setVisibility(View.INVISIBLE);
            }
            if (mMovieRVAdapter != null) {
                mMovieRVAdapter.setOnItemClickListener(new MovieRVAdapter.ClickListener() {
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

                        callingIntent.putExtra(Constants.KEY_TITLE, title);
                        callingIntent.putExtra(Constants.KEY_IMAGE_URL, imageUrl);
                        callingIntent.putExtra(Constants.KEY_RATING, rating);
                        callingIntent.putExtra(Constants.KEY_PLOT, plot);
                        callingIntent.putExtra(Constants.KEY_RELEASE_DATE, releaseDate);
                        callingIntent.putExtra(Constants.KEY_ID, id);

                        startActivity(callingIntent);
                    }
                });
            }
        }


        @Override
        public void onLoaderReset(Loader<ArrayList<Movies>> loader) {

        }

    }

    /**
     * Seperate loader for database data
     */

    public class FavoritesLoader implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            if (mErrorTv.getVisibility() == View.VISIBLE) {
                mErrorTv.setVisibility(View.INVISIBLE);
                moviesRv.setVisibility(View.GONE);
            }


            mProgressBar.setVisibility(View.VISIBLE);
            return new CursorLoader(MovieActivity.this,
                    FavoritesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
            mProgressBar.setVisibility(GONE);
            moviesRv.setVisibility(View.VISIBLE);

            if (data.getCount() > 0) {
                mErrorTv.setVisibility(GONE);
                favoritesAdapter = new FavoritesAdapter(MovieActivity.this, data);

                moviesRv.setLayoutManager(new GridLayoutManager(loader.getContext(), 2));

                moviesRv.setAdapter(favoritesAdapter);

                favoritesAdapter.setOnItemClickListener(new FavoritesAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {

                        if (data.moveToPosition(position)) {

                            int titleIndex = data.getColumnIndex(FavoritesEntry
                                    .COLUMN_TITLE);
                            int ratingIndex = data.getColumnIndex(FavoritesEntry.COLUMN_RATING);
                            int reviewIndex = data.getColumnIndex(FavoritesEntry.COLUMN_REVIEW);
                            int youtubeIndex = data.getColumnIndex(FavoritesEntry
                                    .COLUMN_YOUTUBE_LINK);
                            int releaseDateIndex = data.getColumnIndex(FavoritesEntry
                                    .COLUMN_RELEASE_DATE);
                            int plotIndex = data.getColumnIndex(FavoritesEntry.COLUMN_SYNPOSIS);
                            int movieIdIndex = data.getColumnIndex(FavoritesEntry.COLUMN_MOVIE_ID);


                            String title = data.getString(titleIndex);
                            String rating = data.getString(ratingIndex);
                            String review = data.getString(reviewIndex);
                            String youtubeLink = data.getString(youtubeIndex);
                            String releaseDate = data.getString(releaseDateIndex);
                            String plot = data.getString(plotIndex);
                            int id = data.getInt(movieIdIndex);

                            Intent intent = new Intent(MovieActivity.this, DetailActivity.class);

                            intent.putExtra(Constants.KEY_TITLE, title);
                            intent.putExtra(Constants.KEY_RATING, rating);

                            if (review != null)
                                intent.putExtra(Constants.KEY_REVIEW, review);


                            intent.putExtra(Constants.KEY_YOUTUBE_LINK, youtubeLink);
                            intent.putExtra(Constants.KEY_RELEASE_DATE, releaseDate);
                            intent.putExtra(Constants.KEY_PLOT, plot);
                            intent.putExtra(Constants.KEY_ID, id);

                            startActivity(intent);
                        }

                    }
                });

                return;
            }

            moviesRv.setVisibility(GONE);
            mErrorTv.setText(getString(R.string.favorites_error));
            mErrorTv.setVisibility(View.VISIBLE);

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}



