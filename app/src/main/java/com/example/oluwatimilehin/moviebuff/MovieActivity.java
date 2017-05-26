package com.example.oluwatimilehin.moviebuff;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity {

    private TextView mTv;
    private Bundle bundle = new Bundle();
    final static int MOVIE_LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        mTv = (TextView) findViewById(R.id.tv);
        String s = "top_rated";
        bundle.putString("query", s);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle,new MovieDataLoader());

    }

    public class MovieDataLoader implements LoaderManager.LoaderCallbacks<ArrayList<Movies>> {


        @Override
        public Loader<ArrayList<Movies>> onCreateLoader(int id, final Bundle args) {

            return new AsyncTaskLoader<ArrayList<Movies>>(getApplicationContext()) {

                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public ArrayList<Movies> loadInBackground() {
                    String apiKey = getString(R.string.api_key);
                    ArrayList<Movies> s = NetworkUtils.parseJson(args.getString("query"), apiKey);
                    return s;
                }
            };

        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movies>> loader, ArrayList<Movies> data) {
            String s = data.get(2).getTitle();
            mTv.setText(s);

        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movies>> loader) {

        }
    }
}



