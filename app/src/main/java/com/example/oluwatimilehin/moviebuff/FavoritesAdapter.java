package com.example.oluwatimilehin.moviebuff;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by Oluwatimilehin on 09/07/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class FavoritesAdapter extends CursorAdapter {
    public FavoritesAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
