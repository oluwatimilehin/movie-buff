package com.example.oluwatimilehin.moviebuff.main;

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


    int mId;


    public Movies(String title, String releaseDate, String imageUrl, String plot, String rating,
                  int id){
        mTitle = title;
        mImageUrl = imageUrl;
        mPlot = plot;
        mRating = rating;
        mReleaseDate = releaseDate;
        mId = id;
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

    public int getId() {
        return mId;
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
