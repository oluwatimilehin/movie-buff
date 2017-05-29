package com.example.oluwatimilehin.moviebuff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mToolbarText;
    private ImageView mImageView;
    private TextView ratingTV;
    private TextView userRatingTV;
    private TextView titleTv;
    private ProgressBar loadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarText = (TextView) findViewById(R.id.toolbar_text);
        setSupportActionBar(mToolbar);
        mToolbarText.setText(R.string.movie_detail);

        mImageView = (ImageView) findViewById(R.id.thumbnail_image);
        ratingTV = (TextView) findViewById(R.id.rating_tv);
        titleTv = (TextView) findViewById(R.id.title_tv);
        userRatingTV = (TextView) findViewById(R.id.user_rating_text);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        Intent callingIntent = getIntent();

        final String title = callingIntent.getStringExtra("title");
        final String imagePath = callingIntent.getStringExtra("imageUrl");
        final String userRating = callingIntent.getStringExtra("rating");
        String plot = callingIntent.getStringExtra("plot");
        String releaseDate = callingIntent.getStringExtra("releaseDate");
        String fullUrl = "http://image.tmdb.org/t/p/w780/" + imagePath;

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        final Callback mCallback = new Callback() {
            @Override
            public void onSuccess() {
                ratingTV.append(userRating);
                titleTv.setText(title);
                userRatingTV.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

            }
        };

       Picasso.with(this).load(fullUrl).into(mImageView, mCallback);

    }


    /**
     * Method used to install the Calligraphy library
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
