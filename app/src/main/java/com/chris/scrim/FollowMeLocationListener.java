package com.chris.scrim;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by chris on 2/11/2016.
 */
public class FollowMeLocationListener implements LocationSource, LocationListener {
    private static final String LAST_KNOWN_PREF = "LastKnownPrefFile";
    private static final String LAST_KNOWN_LOCATION_LAT = "Last Known latitude";
    private static final String LAST_KNOWN_LOCATION_LONG = "Last Known longitude";
    //http://www.geomidpoint.com/latlon.html range of latitude
    private static final float NO_LAST_KNOWN_LOCATION = 91;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private LocationManager locationManager;
    private final Criteria criteria = new Criteria();
    private Context mContext;
    private GoogleMap mMap;
    private ProgressDialog waitToReceiveCurLoc;
    private SharedPreferences lastKnownLocation;

    public FollowMeLocationListener(Context theContext, GoogleMap theMap) {
        // Get reference to Location Manager
        locationManager = (LocationManager) theContext.getSystemService(Context.LOCATION_SERVICE);
        lastKnownLocation = theContext.getSharedPreferences(LAST_KNOWN_PREF, Context.MODE_PRIVATE);
        mMap = theMap;
        float lastKnownLatitude = lastKnownLocation.getFloat(LAST_KNOWN_LOCATION_LAT, NO_LAST_KNOWN_LOCATION);
        if(lastKnownLatitude == NO_LAST_KNOWN_LOCATION) {
        //   waitToReceiveCurLoc = ProgressDialog.show(theContext, "Loading", "Please Wait", true);
        } else {
            float lastKnownLongitude = lastKnownLocation.getFloat(LAST_KNOWN_LOCATION_LAT, NO_LAST_KNOWN_LOCATION);
            moveToCurrentLocation(mMap, new LatLng(lastKnownLatitude, lastKnownLongitude));
        }
        mContext = theContext;
        // Specify Location Provider criteria
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        if(ContextCompat.checkSelfPermission(theContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }
    }


    /* Activates this provider. This provider will notify the supplied listener
     * periodically, until you call deactivate().
     * This method is automatically invoked by enabling my-location layer. */
    @Override
    public void activate(OnLocationChangedListener listener) {
    }

    /* Deactivates this provider.
     * This method is automatically invoked by disabling my-location layer. */
    @Override
    public void deactivate() {
    }

    @Override
    public void onLocationChanged(Location location) {
        SharedPreferences.Editor editLastKnownLoc = lastKnownLocation.edit();
        editLastKnownLoc.putFloat(LAST_KNOWN_LOCATION_LAT, (float)location.getLatitude());
        editLastKnownLoc.putFloat(LAST_KNOWN_LOCATION_LONG, (float)location.getLongitude());
        editLastKnownLoc.commit();
        moveToCurrentLocation(mMap, new LatLng(location.getLatitude(), location.getLongitude()));
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
        if(waitToReceiveCurLoc != null) {
            waitToReceiveCurLoc.dismiss();
        }
    }

   public static void moveToCurrentLocation(GoogleMap theMap, LatLng currentLocation) {
       theMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        // Zoom in, animating the camera.
       theMap.animateCamera(CameraUpdateFactory.zoomIn());
       //Zoom out to zoom level 10, animating with a duration of 2 seconds.
       theMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
   }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
