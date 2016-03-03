package com.chris.scrim;


import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chris on 2/4/2016.
 */
public class ScrimArea {
    private static final String TAG = ScrimArea.class.getName();
    private String title;
    private String additionalInfo;
    private String type;
    private Marker scrimMarker;
    private int numSpots;
    private int typeImage;
    private int markerImage;
    private LatLng center;
    private int id;
    private Calendar date;

    public ScrimArea(GoogleMap mMap, LatLng  center, String theName, String theAdditionalInfo,
                     int markerImage, int typeImage, int numSpots, String type, Calendar date){
        id = VitalizeApplication.getUniqueId();
        scrimMarker = mMap.addMarker(new MarkerOptions().position(center).title(title).
                                draggable(true).snippet(additionalInfo));
        this.center = center;

        update(theName, theAdditionalInfo, typeImage, markerImage, numSpots, type, date);
    }
    public ScrimArea() {
    }
    public Calendar getDate() {
        return date;
    }
    public void populateDateText(TextView textView) {
        String dateText = date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH) + " " +  date.get(Calendar.HOUR) +
                ":" + date.get(Calendar.MINUTE);
        if(date.get(Calendar.AM_PM) == Calendar.AM) {
            dateText += "AM";
        } else {
            dateText += "PM";
        }
        textView.setText(dateText);
    }
    public static void loadAllAreasOntoMap(GoogleMap map) {
        for(ScrimArea area: VitalizeApplication.getAllAreas()) {
            area.scrimMarker = map.addMarker(new MarkerOptions().position(area.center).title(area.title).draggable(true).snippet(area.additionalInfo)
                .icon(BitmapDescriptorFactory.fromResource(area.markerImage)));
        }
    }
    public void update(String title, String theAdditionalInfo, int typeImage, int markerImage, int numSpots, String type, Calendar date) {
        this.title = title;
        additionalInfo = theAdditionalInfo;
        this.markerImage = markerImage;
        if(scrimMarker != null) {
            scrimMarker.setIcon(BitmapDescriptorFactory.fromResource(markerImage));
        }
        this.numSpots = numSpots;
        this.type = type;
        this.typeImage = typeImage;
        this.date = date;
    }
    public Marker getScrimMarker () {
        return scrimMarker;
    }

    public static Calendar parseDateOut(String date) {
        Calendar parsedCalendarOut = Calendar.getInstance();
        String[] timeComponents = date.split(" ");
        String dateComponent = timeComponents[0];
        String[] dayAndMonth = dateComponent.split("/");
        parsedCalendarOut.set(Calendar.MONTH, Integer.parseInt(dayAndMonth[0]));
        parsedCalendarOut.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayAndMonth[1]));
        String hourAndDayComponent = timeComponents[1];
        String[] hourAndDay = hourAndDayComponent.split(":");
        parsedCalendarOut.set(Calendar.HOUR, Integer.parseInt(hourAndDay[0]));
        parsedCalendarOut.set(Calendar.MINUTE, Integer.parseInt(hourAndDay[1].substring(0, hourAndDay[1].length() - 2)));
        String amPm = hourAndDay[1].substring(hourAndDay[1].length() - 2, hourAndDay[1].length());
        if(amPm.equals("AM")) {
            parsedCalendarOut.set(Calendar.AM_PM, Calendar.AM);
        } else {
            parsedCalendarOut.set(Calendar.AM_PM, Calendar.PM);
        }
        return parsedCalendarOut;
    }
    public LatLng getCenter() {
        return center;
    }

    @Override
    public String toString() {
        return "ScrimArea{" +
                "title='" + title + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", type='" + type + '\'' +
                ", numSpots=" + numSpots +
                ", center=" + center +
                ", id=" + id +
                '}';
    }

    public int getId() {
        return id;
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
    public void setCenter(LatLng center) {
        this.center = center;
    }
    public void setId(int id) {
        this.id = id;
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
