package com.chris.scrim;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by chris on 2/4/2016.
 */
public class ScrimArea {
    private Circle myCircle;
    private Marker centerOfCircle;
    private GoogleMap mMap;
    public ScrimArea(GoogleMap mMap, LatLng  center ){
        this.mMap = mMap;
        centerOfCircle=mMap.addMarker(new MarkerOptions().position(center).title("new").
                draggable(true).snippet("New bubble")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        CircleOptions       circleAroundMarker = new CircleOptions()
                .center(center)
                .radius(1000)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE);
        myCircle = mMap.addCircle(circleAroundMarker);
    }

    public Circle getCircle() {
        return myCircle;
    }

    public Marker getCenterOfCircle() {
        return centerOfCircle;
    }

    public boolean showIfInCircle(LatLng pointOfInterest) {
        double dx = myCircle.getCenter().latitude - pointOfInterest.latitude;
        double dy = myCircle.getCenter().longitude - pointOfInterest.longitude;
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(dx);
        double dLng = Math.toRadians(dy);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(pointOfInterest.latitude)) *
                        Math.cos(Math.toRadians(myCircle.getCenter().latitude)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (earthRadius * c) <= myCircle.getRadius();
    }

    public void showMarkerMessage() {
        centerOfCircle.showInfoWindow();

    }
    public void remove() {
        centerOfCircle.remove();
        myCircle.remove();;
    }
}
