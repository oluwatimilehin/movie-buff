package com.example.oluwatimilehin.moviebuff;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

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
    public  boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(CONNECTIVITY_SERVICE);

        info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


}
