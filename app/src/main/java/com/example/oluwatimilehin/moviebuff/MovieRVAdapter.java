package com.example.oluwatimilehin.moviebuff;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Oluwatimilehin on 26/05/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class MovieRVAdapter extends RecyclerView.Adapter<MovieRVAdapter.MovieViewHolder> {

    ArrayList<Movies> mMovies = new ArrayList<Movies>();

    public MovieRVAdapter(ArrayList<Movies> movies){
        mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent,
                false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String imagePath = mMovies.get(position).getImageUrl();
        String url = "http://image.tmdb.org/t/p/w185/" + imagePath;

        holder.titleTextView.setText(mMovies.get(position).getTitle());
        holder.dateTextView.setText(mMovies.get(position).getYear());
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView dateTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            dateTextView = (TextView) itemView.findViewById(R.id.tv_year);

        }
    }


}
