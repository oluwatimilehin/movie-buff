package com.example.oluwatimilehin.moviebuff;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Oluwatimilehin on 08/07/2017.
 * oluwatimilehinadeniran@gmail.com.
 */

public class MasterActivity extends AppCompatActivity {
    protected NetworkInfo info;


    /**
     * This method checks for the internet connection status
     *
     * @return
     */
    public  boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(CONNECTIVITY_SERVICE);

        info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void showErrorScreen(TextView mErrorTv, ProgressBar mProgressBar){
        mErrorTv.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorTv.setText(getString(R.string.internet_error));
    }


}
