package com.example.oluwatimilehin.moviebuff.details;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.oluwatimilehin.moviebuff.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.os.Build.VERSION_CODES.M;

public class DetailActivity extends AppCompatActivity {

    private static final int DETAILS_LOADER_ID = 916;
    String fullUrl;
    ArrayList<Reviews> reviews;
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
    private Callback mCallback;
    private TextView userReview;

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

        starImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Drawable drawable = starImage.getDrawable().mutate();
                if(drawable.getColorFilter() != null)
                Log.d("HEREHERE", drawable.getColorFilter().toString());

                if(drawable.getColorFilter() != null && drawable.getColorFilter().toString()
                        .equals("android.graphics.PorterDuffColorFilter@db6dbec7")) {
                    drawable.setColorFilter(getResources().getColor(R.color.textColor), PorterDuff.Mode
                            .SRC_ATOP);
                }
                else{
                    drawable.setColorFilter(getResources().getColor(R.color.orange_star), PorterDuff.Mode.SRC_ATOP);
                }


            }

        });


        final String title = callingIntent.getStringExtra("title").toUpperCase();
        final String imagePath = callingIntent.getStringExtra("imageUrl");
        final String userRating = callingIntent.getStringExtra("rating");
        final String plot = callingIntent.getStringExtra("plot");
        final String releaseDate = callingIntent.getStringExtra("releaseDate");
        final int id = callingIntent.getIntExtra("id", 1);
        fullUrl = "http://image.tmdb.org/t/p/w780/" + imagePath;


        mCallback = new Callback() {
            @Override
            public void onSuccess() {
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
                userReview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                errorTV.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.INVISIBLE);
            }
        };

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
            upIcon.setColorFilter(getResources().getColor(R.color.textColor, null), PorterDuff.Mode.SRC_ATOP);
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

            if (true) {
                return new DetailsLoader(DetailActivity.this, apiKey, args);
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Bundle> loader, Bundle data) {

            if (data != null) {
                final String youtubeKey = data.getString("youtube_key");
                reviews = data.getParcelableArrayList("reviews");
                final String youtubeLink = "https://www.youtube.com/watch?v=" + youtubeKey;

                if (reviews.size() > 0) {
                    userReview.setText(reviews.get(0).getContent() + " -" + reviews.get(0).getAuthor());
                }

                Picasso.with(DetailActivity.this).load(fullUrl).into(mImageView, mCallback);
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
        }

        @Override
        public void onLoaderReset(Loader<Bundle> loader) {

        }
    }
}
