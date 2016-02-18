package com.chris.scrim;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by chris on 2/4/2016.
 */
public class ScrimArea {
    private String name;
    private String Description;
    public ScrimArea(GoogleMap mMap, LatLng  center, String theName, String theDescription,
                     int markerImage){

        name = theName;
        Description = theDescription;
        mMap.addMarker(new MarkerOptions().position(center).title(name).
                draggable(true).snippet(Description)
                .icon(BitmapDescriptorFactory.fromResource(markerImage)));
    }
}
