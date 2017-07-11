package com.example.oluwatimilehin.moviebuff;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.oluwatimilehin.moviebuff.imageutils.BitMapUtils;

import static com.example.oluwatimilehin.moviebuff.data.MovieContract.FavoritesEntry;

/**
 * Created by Oluwatimilehin on 09/07/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private CursorAdapter mCursorAdapter;
    private Context mContext;
    private static ClickListener sClickListener;

    public FavoritesAdapter(Context context, Cursor c) {
        mContext = context;

        mCursorAdapter = new CursorAdapter(context, c, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.favorites_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                TextView titleTv = (TextView) view.findViewById(R.id.tv_title);
                TextView yearTv = (TextView) view.findViewById(R.id.tv_year);

                int imageIndex;
                int titleIndex;
                int yearIndex;


                imageIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_IMAGE);
                titleIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_TITLE);
                yearIndex = cursor.getColumnIndex(FavoritesEntry.COLUMN_RELEASE_DATE);

                String year = cursor.getString(yearIndex).substring(0,4);

                byte[] imageByte = cursor.getBlob(imageIndex);
                Bitmap imageBitmap = BitMapUtils.getImage(imageByte);

                imageView.setImageBitmap(imageBitmap);
                titleTv.setText(cursor.getString(titleIndex));
                yearTv.setText(year);
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());

    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ViewHolder(View itemView) {

            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener listener){
        FavoritesAdapter.sClickListener = listener;
    }

    public interface ClickListener{
        void onItemClick(int position, View v);
    }



}
