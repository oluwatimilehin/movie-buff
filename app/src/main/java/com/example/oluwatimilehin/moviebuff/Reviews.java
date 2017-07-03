package com.example.oluwatimilehin.moviebuff;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Oluwatimilehin on 03/07/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class Reviews implements Parcelable {

    private String CREATOR;
    private String author;
    private String content;

    public Reviews(String author, String content) {
        this.author = author;
        this.content = content;
    }


    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
