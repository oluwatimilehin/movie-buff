package com.example.oluwatimilehin.moviebuff;

import android.net.Uri;
import android.os.Bundle;
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

public class NetworkUtils extends AppCompatActivity {

    static final String MOVIE_SCHEME = "https";
    static final String MOVIE_AUTHORITY = "api.themoviedb.org";
    static final String reviewsKey = "reviews";
    static final String videosKey = "videos";


    public static ArrayList<Movies> parseJson(String key, String apiKey) {
        URL url = buildUrl(key, apiKey);
        String jsonResult = extractResult(url);

        ArrayList<Movies> movies = new ArrayList<Movies>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject results = jsonArray.getJSONObject(i);
                    String title = results.getString("original_title");
                    String release_date = results.getString("release_date");
                    String path = results.getString("poster_path");
                    String synopsis = results.getString("overview");
                    String rating = results.getString("vote_average");
                    int id = results.getInt("id");
                    movies.add(new Movies(title, release_date, path, synopsis, rating, id));
                }
            }

        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
            return null;

        }
        return movies;

    }

    public static Bundle parseJson(int id, String apiKey) {

        Bundle bundle = new Bundle();
        URL reviewsURL = buildUrl(id, reviewsKey, apiKey);
        URL videosURL = buildUrl(id, videosKey, apiKey);

        ArrayList<Reviews> reviews = new ArrayList<Reviews>();
        String reviewsString = extractResult(reviewsURL);
        String videosString = extractResult(videosURL);
        String youtubeKey = "";
        String author;
        String content;

        try{
            JSONObject videosJSON =new JSONObject(videosString);
            JSONArray videosArray = videosJSON.getJSONArray("results");
            for(int i = 0; i < videosArray.length(); i ++){
                JSONObject currentObject = videosArray.getJSONObject(i);
                String type = currentObject.getString("type");
                if(type.equals("Trailer")){
                    youtubeKey = currentObject.getString("key");
                    break;
                }
            }

            JSONObject reviewsJSON = new JSONObject(reviewsString);
            JSONArray reviewsArray = reviewsJSON.getJSONArray("results");
            for(int i = 0; i < reviewsArray.length(); i++){
                JSONObject currentObject = reviewsArray.getJSONObject(i);
                author = currentObject.getString("author");
                content = currentObject.getString("content");
                reviews.add(new Reviews(author, content));
            }

        } catch (JSONException | NullPointerException e){
            e.printStackTrace();
            return null;
        }

        bundle.putString("youtube_key", youtubeKey );
        bundle.putParcelableArrayList("reviews", reviews);
        return  bundle;
    }


    private static URL buildUrl(int id, String key, String apiKey) {
        URL url = null;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MOVIE_SCHEME)
                .authority(MOVIE_AUTHORITY)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(String.valueOf(id))
                .appendPath(key)
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
     * Construct the URL used to make a connection
     *
     * @param keyword
     * @return
     */
    private static URL buildUrl(String keyword, String apiKey) {
        URL url = null;

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

    private static StringBuffer makeHttRequest(URL url) {

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        StringBuffer buffer = new StringBuffer();

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

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

        return buffer;
    }

    /**
     * Method to return the JSON response from the API call
     *
     * @return
     */
    private static String extractResult(URL url) {

        StringBuffer buffer = makeHttRequest(url);
        return buffer != null ? buffer.toString() : null;
    }
}
