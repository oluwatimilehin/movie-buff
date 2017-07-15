package com.example.oluwatimilehin.moviebuff.details;

import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.oluwatimilehin.moviebuff.Constants;
import com.example.oluwatimilehin.moviebuff.MasterActivity;
import com.example.oluwatimilehin.moviebuff.R;
import com.example.oluwatimilehin.moviebuff.data.MovieContract.FavoritesEntry;
import com.example.oluwatimilehin.moviebuff.imageutils.BitMapUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import static com.example.oluwatimilehin.moviebuff.Constants.KEY_YOUTUBE_LINK;

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
    Bitmap imageBitmap;
    Drawable drawable;
    Target bitmapTarget;
    String youtubeKey;
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
    private Button readMoreButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarText = (TextView) findViewById(R.id.toolbar_text);
        setSupportActionBar(mToolbar);
        mToolbarText.setText(R.string.movie_detail);
        mToolbarText.setGravity(Gravity.NO_GRAVITY);
        mToolbarText.setPadding(100, 0, 0, 0);

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
        readMoreButton = (Button) findViewById(R.id.read_more_btn);


        mImageView.setColorFilter(getResources().getColor(R.color.cardview_dark_background),
                PorterDuff.Mode.MULTIPLY);


        Intent callingIntent = getIntent();

        title = callingIntent.getStringExtra(Constants.KEY_TITLE).toUpperCase();
        userRating = callingIntent.getStringExtra(Constants.KEY_RATING);
        plot = callingIntent.getStringExtra(Constants.KEY_PLOT);
        releaseDate = callingIntent.getStringExtra(Constants.KEY_RELEASE_DATE);
        id = callingIntent.getIntExtra("id", 1);

        if (callingIntent.hasExtra(Constants.KEY_YOUTUBE_LINK)) {
            youtubeLink = callingIntent.getStringExtra(Constants.KEY_YOUTUBE_LINK);
            imageBitmap = loadImage();
            if (callingIntent.hasExtra(Constants.KEY_REVIEW)) {
                review = callingIntent.getStringExtra(Constants.KEY_REVIEW);
            }

            loadViews();
        } else {
            imagePath = callingIntent.getStringExtra(Constants.KEY_IMAGE_URL);
            fullUrl = "http://image.tmdb.org/t/p/w780/" + imagePath;
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add youtube intent
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd" +
                        ".youtube:" + youtubeKey));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));

                if(youtubeKey != null) {
                    try {
                        startActivity(appIntent);

                    } catch (ActivityNotFoundException e) {
                        startActivity(webIntent);
                    }
                }
                else{
                    startActivity(webIntent);
                }
            }
        });

        //Longclick listener to share youtube link
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

        drawable = starImage.getDrawable().mutate();

        if (isInFavorites()) {
            drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R
                    .color.orange_star), PorterDuff.Mode
                    .SRC_ATOP);
        }

        starImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //If movie has been added to favorites, delete it.
                if (drawable.getColorFilter() != null) {
                    String selectionClause = FavoritesEntry.COLUMN_TITLE + " = ?";
                    String[] selectionArgs = {title};
                    int rowsDeleted;

                    rowsDeleted = getContentResolver().delete(FavoritesEntry.CONTENT_URI,
                            selectionClause, selectionArgs);
                    if (rowsDeleted > 0) {
                        drawable.clearColorFilter();
                    }

                } else { //Insert into favorites table

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

                    if (review != null)
                        newValues.put(FavoritesEntry.COLUMN_REVIEW, review);

                    mUri = getContentResolver().insert(FavoritesEntry.CONTENT_URI, newValues);

                    if (mUri != null) {
                        drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R
                                .color.orange_star), PorterDuff.Mode
                                .SRC_ATOP);
                    }
                }
            }

        });

        bitmapTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
               // mImageView.setImageBitmap(bitmap);
                imageBitmap = bitmap;
                loadViews();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                errorTV.setVisibility(View.VISIBLE);
                loadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };


        readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int collapsedMaxLines = 4;
                ObjectAnimator animation = ObjectAnimator.ofInt(userReview, "maxLines",
                        userReview.getMaxLines() == collapsedMaxLines ? userReview.getLineCount() : collapsedMaxLines);
                animation.setDuration(200).start();

                if (readMoreButton.getText().equals(getString(R.string.read_more))) {
                    readMoreButton.setText
                            (getString(R.string.show_less));
                } else {
                    readMoreButton.setText(getString(R.string
                            .read_more));
                }
            }
        });

        //Start the loader only if the movie is not in the database
        if (!callingIntent.hasExtra(KEY_YOUTUBE_LINK)) {

            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            getSupportLoaderManager().initLoader(DETAILS_LOADER_ID, bundle, new DetailsDataLoader());
        }

    }


    private void loadViews() {

        ratingTV.setText(userRating);
        titleTV.setText(title);
        releaseDateTV.setText(releaseDate);
        plotTV.setText(plot);
        starImage.setVisibility(View.VISIBLE);
        mImageView.setImageBitmap(imageBitmap);
        userRatingStringTV.setVisibility(View.VISIBLE);
        releaseDateStringTV.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
        errorTV.setVisibility(View.GONE);

        if (review != null) {
            userReview.setText(review);
            reviewLabel.setVisibility(View.VISIBLE);
            userReview.setVisibility(View.VISIBLE);

            userReview.post(new Runnable() {
                @Override
                public void run() {
                    if (userReview.getLineCount() > userReview.getMaxLines()) {

                        readMoreButton.setVisibility(View.VISIBLE);
                    }
                }
            });

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Raleway-Light.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );

    }

    private boolean isInFavorites() {
        Cursor cursor = getContentResolver().query(FavoritesEntry.CONTENT_URI,
                new String[]{FavoritesEntry.COLUMN_MOVIE_ID},
                FavoritesEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{String.valueOf(id)},
                null,
                null
        );

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        return false;
    }

    private Bitmap loadImage(){
        Cursor cursor = getContentResolver().query(FavoritesEntry.CONTENT_URI,
                new String[]{FavoritesEntry.COLUMN_IMAGE},
                FavoritesEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{String.valueOf(id)},
                null,
                null);
        Bitmap imageBitmap = null;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int imageIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_IMAGE);

                byte[] image = cursor.getBlob(imageIndex);

               imageBitmap  = BitMapUtils.getImage(image);

                cursor.close();
            }
        }

        return imageBitmap;
    }

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
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    public class DetailsDataLoader implements LoaderManager.LoaderCallbacks<Bundle> {

        @Override
        public Loader<Bundle> onCreateLoader(int id, Bundle args) {

            String apiKey = getString(R.string.api_key);

            if (isConnected(getApplicationContext())) {
                return new DetailsLoader(DetailActivity.this, apiKey, args);
            }
            showErrorScreen(errorTV, loadingIndicator);
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Bundle> loader, Bundle data) {

            if (data != null) {
                youtubeKey = data.getString("youtube_key");
                reviews = data.getParcelableArrayList("reviews");
                if (reviews.size() > 0) {
                    review = reviews.get(0).getContent() + " -" + reviews.get(0).getAuthor();
                }

                youtubeLink = "https://www.youtube.com/watch?v=" + youtubeKey;


                if (review != null) {
                    userReview.setText(review);
                }

                Picasso.with(DetailActivity.this)
                        .load(fullUrl)
                        .into(bitmapTarget);

            } else {
                showErrorScreen(errorTV, loadingIndicator);
            }
        }

        @Override
        public void onLoaderReset(Loader<Bundle> loader) {
        }
    }
}
