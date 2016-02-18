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
    private String title;
    private String Description;
    private String type;
    private Marker scrimMarker;
    private int numSpots;
    private int typeImage;
    public ScrimArea(GoogleMap mMap, LatLng  center, String theName, String theDescription,
                     int markerImage, int typeImage, int numSpots, String type){

        title = theName;
        Description = theDescription;
        scrimMarker = mMap.addMarker(new MarkerOptions().position(center).title(title).
                                draggable(true).snippet(Description)
                                .icon(BitmapDescriptorFactory.fromResource(markerImage)));
        this.typeImage = typeImage;
        this.numSpots = numSpots;
        this.type = type;
    }

    public Marker getScrimMarker() {
        return scrimMarker;
    }
    public String getTitle() {
        return title;
    }
    public int getTypeImage() {
        return typeImage;
    }
    public int getNumSpots () {
        return numSpots;
    }
    public String getType () {
        return type;
    }
}
