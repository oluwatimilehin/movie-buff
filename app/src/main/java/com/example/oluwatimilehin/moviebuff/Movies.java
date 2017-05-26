package com.example.oluwatimilehin.moviebuff;

/**
 * Created by Oluwatimilehin on 25/05/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class Movies {

    String mTitle;
    String mImageUrl;
    String mYear;
    String mPlot;
    String mRating;
    String mReleaseDate;


    public Movies(String title, String releaseDate, String imageUrl, String plot, String rating){
        mTitle = title;
        mImageUrl = imageUrl;
        mPlot = plot;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    public String getTitle() {

        return mTitle;
    }

    public String getImageUrl() {

        return mImageUrl;
    }

    public String getYear() {
        mYear = mReleaseDate.substring(0, 4);
        return mYear;
    }

    public String getPlot() {
        return mPlot;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

}
