package com.example.mad.shellpandroid.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.mad.shellpandroid.R;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.mad.shellpandroid.navigation.lib.Route;
import com.example.mad.shellpandroid.navigation.lib.Routing;
import com.example.mad.shellpandroid.navigation.lib.RoutingListener;

import java.text.DateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RoutingListener, com.google.android.gms.location.LocationListener {
    /*
    TODO: remove old routes
    add arrow that points to which direction user is moving
    have optional my location button
    add markers to autocomplete
    clear button (for autocomplete)
    get current location
    add filter?

    simulate google maps app?
    tnav may help
    attributions
    possitble usage limits upgrade
    add walking icon
     */
    /*
    Completed Features:
    map loaded
    pathing
    add search
    autocomplete
    get zoom level correct
    dismiss keyboard post search
    single line text
    rounded corners
    better margins


     */
    LocationRequest mLocationRequest;
    private Boolean mRequestLocationUpdates;
    private Location myLocation;
    protected  LatLng mStart;
    protected  LatLng mEnd;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteViewStart;
    private AutoCompleteTextView mAutocompleteViewDest;
    private LinearLayout filler;
    private static PointF southWestBound = new PointF(38.977894f, -76.958070f);
    private static PointF northEastBound = new PointF(39.002911f, -76.924510f);
    private static final LatLngBounds BOUNDS_COLLGE_PARK = new LatLngBounds(
            new LatLng(southWestBound.x, southWestBound.y), new LatLng(northEastBound.x,northEastBound.y)); // southwest then northeast bounds of UMD

    private static final String TAG = "MapsActivity";
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private FusedLocationProviderApi mFusedLocationApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this) //used random number??-------------fix
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        //mFusedLocationApi = new FusedLocationProviderApi() {
        mRequestLocationUpdates = true; // should be users option

        mAutocompleteViewStart = (AutoCompleteTextView) findViewById(R.id.autocomplete_start);
        mAutocompleteViewStart.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteViewStart.setSelected(false);

        mAutocompleteViewDest = (AutoCompleteTextView) findViewById(R.id.autocomplete_dest);
        mAutocompleteViewDest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!mAutocompleteViewStart.hasFocus()){
                    //showBothBars(true);
                }
            }
        });
        mAutocompleteViewDest.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteViewDest.setSelected(false);

        filler = (LinearLayout) findViewById(R.id.filler);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        //AutocompleteFilter filter = new AutocompleteFilter();
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_COLLGE_PARK, null);


        mAutocompleteViewStart.setAdapter(mAdapter);
        mAutocompleteViewDest.setAdapter(mAdapter);


    }

    @Override
    public void onConnected(Bundle bundle) {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (myLocation != null) {
            mStart = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }
        if (mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        mStart = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();


    }

    private void updateUI() {
        if ( mStart != null && mEnd != null){
            displayRoute(mStart,mEnd);
            double midX = (mEnd.latitude + mStart.latitude)/2;
            double midY = (mEnd.longitude + mStart.longitude)/2;
            LatLng middle = new LatLng(midX, midY);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middle, 14));
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mAutocompleteViewStart.getWindowToken(), 0);
        } else {
            Toast.makeText(getApplicationContext(), "Current location unavailable",Toast.LENGTH_SHORT).show();
        }

    }
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    public void onProviderEnabled(String provider) {

    }
    public void onProviderDisabled(String provider) {

    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            //Log.i(TAG, "Autocomplete item selected: " + item.description);
            //showBothBars(false);
            //showBothBars();

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            //Toast.makeText(getApplicationContext(), "Clicked: " + item.description,Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

    }

    // mMap can set get user location, also can check if it's currently enabled
    private void setUpMap() {
        final double lat = 38.98692; // LOCATION of UMD
        final double lng = -76.94255;
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(10, 10))
                .title("aoeu"));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("UMD"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lng), 14));
        createLocationRequest();


    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(400);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void setStartToCurrentLocaton(){
        mMap.setMyLocationEnabled(true);
        if ( !mMap.isMyLocationEnabled()){
            showBothBars(true);
        } else {
            Location loc = mMap.getMyLocation();
            if ( loc != null ){
                LatLng userLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
                if ( userLoc != null){
                    mStart = userLoc;
                }

            }
        }
    }
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            mEnd = place.getLatLng();
            updateUI();

            // Format details of the place for display and show it in a TextView.
/*            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));*/

/*            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = null;//PlacePicker.getAttributions();//places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }*/

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
/*    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }*/
    private void displayRoute(LatLng start, LatLng end){
        // mStart = new LatLng(southWestBound.x, southWestBound.y);
        //mEnd = new LatLng(northEastBound.x, northEastBound.y);
        mStart = start;
        mEnd = end;
        Routing routing = new Routing(Routing.TravelMode.WALKING);
        routing.registerListener(this);
        routing.execute(start, end);
    }
    private void showBothBars(boolean show){
        if (show){
            filler.setVisibility(View.GONE);
            mAutocompleteViewStart.setVisibility(View.VISIBLE);
        } else {
            filler.setVisibility(View.VISIBLE);
            mAutocompleteViewStart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRoutingFailure() {
        Toast.makeText(getApplicationContext(), "Route could not be determined",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(PolylineOptions mPolyOptions, Route route) {
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.BLUE);
        polyOptions.width(10);
        polyOptions.addAll(mPolyOptions.getPoints());
        mMap.addPolyline(polyOptions);

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(mStart);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        mMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(mEnd);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        mMap.addMarker(options);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}