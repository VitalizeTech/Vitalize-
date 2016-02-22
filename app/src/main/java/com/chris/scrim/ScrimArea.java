package com.chris.scrim;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by chris on 2/4/2016.
 */
public class ScrimArea {
    private String title;
    private String additionalInfo;
    private String type;
    private Marker scrimMarker;
    private int numSpots;
    private int typeImage;
    private int markerImage;
    private LatLng center;
    public ScrimArea(GoogleMap mMap, LatLng  center, String theName, String theDescription,
                     int markerImage, int typeImage, int numSpots, String type){
        scrimMarker = mMap.addMarker(new MarkerOptions().position(center).title(title).
                                draggable(true).snippet(additionalInfo));
        update(theName, theDescription, typeImage, markerImage, numSpots, type);
        this.center = center;
    }
    public static void loadAllAreasOntoMap(GoogleMap map) {
        for(ScrimArea area: VitalizeApplication.getAllAreas()) {
            area.scrimMarker = map.addMarker(new MarkerOptions().position(area.center).title(area.title).draggable(true).snippet(area.additionalInfo)
                .icon(BitmapDescriptorFactory.fromResource(area.markerImage)));
        }
    }
    public void update(String title, String description, int typeImage, int markerImage, int numSpots, String type) {
        this.title = title;
        additionalInfo = description;
        this.markerImage = markerImage;
        scrimMarker.setIcon(BitmapDescriptorFactory.fromResource(markerImage));
        this.numSpots = numSpots;
        this.type = type;
        this.typeImage = typeImage;
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
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    public static ScrimArea getScrimAreaOfMarker(Marker toSearchFor, List<ScrimArea> scrimAreaList) {
        for(int k=0; k<scrimAreaList.size(); k++) {
            //marker comparison won't work, creating new ones in memory
            if(scrimAreaList.get(k).center.equals(toSearchFor.getPosition())){
                return scrimAreaList.get(k);
            }
        }
        return null;
    }

}
