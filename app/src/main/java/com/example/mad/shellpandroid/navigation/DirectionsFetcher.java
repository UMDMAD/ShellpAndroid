package com.example.mad.shellpandroid.navigation;

/**
 * Created by Chris on 6/8/2015.
 */
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectionsFetcher extends AsyncTask<URL, Integer, String> {
    private List<LatLng> latLngs = new ArrayList<LatLng>();

    @Override
    protected String doInBackground(URL... params) {
        return null;
    }
}
