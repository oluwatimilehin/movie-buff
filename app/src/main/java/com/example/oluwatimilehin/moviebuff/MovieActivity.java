package com.example.oluwatimilehin.moviebuff;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity{

    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        mTv = (TextView) findViewById(R.id.tv);
        String s = "top_rated";
         new FetchMovieData().execute(s);
    }


    private class FetchMovieData extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... strings) {

            ArrayList<Movies> s = NetworkUtils.parseJson(strings[0]);

            return  s.get(2).getTitle();
        }

        @Override
        protected void onPostExecute(String s) {

            mTv.setText(s);
        }
    }
}

