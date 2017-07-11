package com.example.oluwatimilehin.moviebuff.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oluwatimilehin.moviebuff.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Oluwatimilehin on 26/05/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class MovieRVAdapter extends RecyclerView.Adapter<MovieRVAdapter.MovieViewHolder> {

    private static ClickListener clickListener;
    private ArrayList<Movies> mMovies = new ArrayList<Movies>();

    public MovieRVAdapter(ArrayList<Movies> movies) {
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
        String url = "http://image.tmdb.org/t/p/w500/" + imagePath;

        Picasso.with(holder.imageView.getContext()).load(url).into(holder.imageView);
        holder.titleTextView.setText(mMovies.get(position).getTitle());
        holder.dateTextView.setText(mMovies.get(position).getYear());
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void setOnItemClickListener(ClickListener listener) {
        MovieRVAdapter.clickListener = listener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView titleTextView;
        TextView dateTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            dateTextView = (TextView) itemView.findViewById(R.id.tv_year);

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

}
