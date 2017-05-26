package com.example.oluwatimilehin.moviebuff;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Oluwatimilehin on 25/05/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public  class NetworkUtils extends AppCompatActivity {


    public static ArrayList<Movies> parseJson(String key, String apiKey) {
        URL url = buildUrl(key, apiKey);
        String jsonResult = extractResult(url);
        ArrayList<Movies> movies = new ArrayList<Movies>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            JSONArray  jsonArray = jsonObject.getJSONArray("results");
            for(int i = 0; i < jsonArray.length(); i ++){
                JSONObject results = jsonArray.getJSONObject(i);
                String title = results.getString("title");
                String year = results.getString("release_date");
                String path = results.getString("poster_path");
                movies.add(new Movies(title, year, path));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  movies;

    }


    /**
     * Construct the URL used to make a connection
     *
     * @param keyword
     * @return
     */
    private static URL buildUrl(String keyword, String apiKey ) {
        URL url = null;

        final String MOVIE_SCHEME = "https";
        final String MOVIE_AUTHORITY = "api.themoviedb.org";

        Uri.Builder builder = new Uri.Builder();
            builder.scheme(MOVIE_SCHEME)
                    .authority(MOVIE_AUTHORITY)
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(keyword)
                    .appendQueryParameter("api_key", apiKey)
                    .build();

        try {
            url = new URL(builder.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Method to return the JSON response from the API call
     *
     * @return
     */
    private static String extractResult(URL url) {

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;

        String movieJSON = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            movieJSON = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return movieJSON;
    }
}
