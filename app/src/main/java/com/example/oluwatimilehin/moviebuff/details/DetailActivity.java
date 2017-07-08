package com.example.oluwatimilehin.moviebuff.details;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oluwatimilehin.moviebuff.MasterActivity;
import com.example.oluwatimilehin.moviebuff.R;
import com.example.oluwatimilehin.moviebuff.data.MovieContract.FavoritesEntry;
import com.example.oluwatimilehin.moviebuff.imageutils.BitMapUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.os.Build.VERSION_CODES.M;

public class DetailActivity extends MasterActivity {

    private static final int DETAILS_LOADER_ID = 916;
    String fullUrl;
    String title;
    String imagePath;
    String userRating;
    String plot;
    String releaseDate;
    String youtubeLink;
    int id;
    ArrayList<Reviews> reviews;
    String review = null;
    private Toolbar mToolbar;
    private TextView mToolbarText;
    private ImageView mImageView;
    private TextView ratingTV;
    private TextView userRatingStringTV;
    private TextView releaseDateStringTV;
    private TextView releaseDateTV;
    private TextView titleTV;
    private TextView plotTV;
    private TextView errorTV;
    private ProgressBar loadingIndicator;
    private ImageView starImage;
    private ImageView playButton;
    private TextView reviewLabel;
    private TextView userReview;
    Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarText = (TextView) findViewById(R.id.toolbar_text);
        setSupportActionBar(mToolbar);
        mToolbarText.setText(R.string.movie_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userReview = (TextView) findViewById(R.id.user_review);
        mImageView = (ImageView) findViewById(R.id.thumbnail_image);
        errorTV = (TextView) findViewById(R.id.error_tv);
        ratingTV = (TextView) findViewById(R.id.rating_tv);
        titleTV = (TextView) findViewById(R.id.title_tv);
        userRatingStringTV = (TextView) findViewById(R.id.user_rating_text);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        releaseDateStringTV = (TextView) findViewById(R.id.release_date_text);
        releaseDateTV = (TextView) findViewById(R.id.release_date);
        plotTV = (TextView) findViewById(R.id.plot_tv);
        starImage = (ImageView) findViewById(R.id.star);
        playButton = (ImageView) findViewById(R.id.play_button);
        reviewLabel = (TextView) findViewById(R.id.user_reviews_label);


        Intent callingIntent = getIntent();

        mImageView.setColorFilter(getResources().getColor(R.color.cardview_dark_background),
                PorterDuff.Mode.MULTIPLY);

        title = callingIntent.getStringExtra("title").toUpperCase();
        imagePath = callingIntent.getStringExtra("imageUrl");
        userRating = callingIntent.getStringExtra("rating");
        plot = callingIntent.getStringExtra("plot");
        releaseDate = callingIntent.getStringExtra("releaseDate");
        id = callingIntent.getIntExtra("id", 1);
        fullUrl = "http://image.tmdb.org/t/p/w780/" + imagePath;


        starImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Drawable drawable = starImage.getDrawable().mutate();

                if (drawable.getColorFilter() != null) {
                    drawable.clearColorFilter();
                }  else {

                    Uri mUri;

                    ContentValues newValues = new ContentValues();
                    newValues.put(FavoritesEntry.COLUMN_MOVIE_ID, id);
                    newValues.put(FavoritesEntry.COLUMN_RATING, userRating);
                    newValues.put(FavoritesEntry.COLUMN_RELEASE_DATE, releaseDate);
                    newValues.put(FavoritesEntry.COLUMN_TITLE, title);
                    newValues.put(FavoritesEntry.COLUMN_SYNPOSIS, plot);
                    newValues.put(FavoritesEntry.COLUMN_YOUTUBE_LINK, youtubeLink);

                    byte[] imageByte = BitMapUtils.getBytes(imageBitmap);
                    newValues.put(FavoritesEntry.COLUMN_IMAGE, imageByte);

                    if(review != null)
                    newValues.put(FavoritesEntry.COLUMN_REVIEW, review);

                    mUri = getContentResolver().insert(FavoritesEntry.CONTENT_URI, newValues);

                    if(mUri != null){
                        drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(),R
                                .color.orange_star), PorterDuff.Mode
                                .SRC_ATOP);
                    }
                }
            }

        });


        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        getSupportLoaderManager().initLoader(DETAILS_LOADER_ID, bundle, new DetailsDataLoader());

    }

    @Override
    protected void onResume() {
        super.onResume();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }

    @RequiresApi(api = M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Drawable upIcon = mToolbar.getNavigationIcon();

        if (upIcon != null) {
            upIcon.mutate();
            upIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.textColor),
                    PorterDuff
                    .Mode.SRC_ATOP);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public class DetailsDataLoader implements LoaderManager.LoaderCallbacks<Bundle> {

        @Override
        public Loader<Bundle> onCreateLoader(int id, Bundle args) {

            String apiKey = getString(R.string.api_key);

            if (isConnected(getApplicationContext())) {
                return new DetailsLoader(DetailActivity.this, apiKey, args);
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Bundle> loader, Bundle data) {

            if (data != null) {
                final String youtubeKey = data.getString("youtube_key");
                reviews = data.getParcelableArrayList("reviews");
                if(reviews.size() > 0) {
                    review = reviews.get(0).getContent() + " -" + reviews.get(0).getAuthor();
                }

                 youtubeLink = "https://www.youtube.com/watch?v=" + youtubeKey;


                if (review != null) {
                    userReview.setText(review);
                }

                Picasso.with(DetailActivity.this)
                        .load(fullUrl)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                mImageView.setImageBitmap(bitmap);
                                ratingTV.setText(userRating);
                                titleTV.setText(title);
                                releaseDateTV.setText(releaseDate);
                                plotTV.setText(plot);
                                starImage.setVisibility(View.VISIBLE);
                                userRatingStringTV.setVisibility(View.VISIBLE);
                                releaseDateStringTV.setVisibility(View.VISIBLE);
                                loadingIndicator.setVisibility(View.GONE);
                                playButton.setVisibility(View.VISIBLE);
                                if (reviews.size() > 0) {
                                    reviewLabel.setVisibility(View.VISIBLE);
                                }
                                imageBitmap = bitmap;
                                userReview.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                errorTV.setVisibility(View.VISIBLE);
                                loadingIndicator.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd" +
                                ".youtube:" + youtubeKey));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));

                        try {
                            startActivity(appIntent);
                        } catch (ActivityNotFoundException e) {
                            startActivity(webIntent);
                        }
                    }
                });

                mImageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, youtubeLink);
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Youtube link");
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        return true;
                    }
                });
            }
            else{
                Toast.makeText(getApplicationContext(), "Please check your internet connection " +
                        "and try again", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<Bundle> loader) {

        }
    }
}
