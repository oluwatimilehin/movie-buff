package com.example.oluwatimilehin.moviebuff;

/**
 * Created by Oluwatimilehin on 25/05/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class Movies {

    String mTitle;
    String mImageUrl;
    String mYear;

    public Movies(String title, String year, String imageUrl){
        mTitle = title;
        mYear = year;
        mImageUrl = imageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getYear() {
        String yearString = mYear.substring(0, 4);
        return yearString;
    }

}
