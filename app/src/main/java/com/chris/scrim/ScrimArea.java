package com.chris.scrim;


import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;
import java.util.List;

/**
 * Created by chris on 2/4/2016.
 */
@JsonIgnoreProperties(value = {"scrimMarker", "center"}, ignoreUnknown = true)
public class ScrimArea {
    private static final String TAG = ScrimArea.class.getName();
    private Marker scrimMarker;
    private String title;
    private String additionalInfo;
    private String type;
    private int numSpots;
    private int typeImage;
    private int markerImage;
    private LatLng center;
    private double latitude;
    private double longitude;
    private String id;
    private long date;

    public ScrimArea(GoogleMap mMap, LatLng  center, String theName, String theAdditionalInfo,
                     int markerImage, int typeImage, int numSpots, String type, Calendar date){
        scrimMarker = mMap.addMarker(new MarkerOptions().position(center).title(getTitle()).
                draggable(true).snippet(getAdditionalInfo()));
        this.center  = center;
        this.latitude = center.latitude;
        this.longitude = center.longitude;

        update(theName, theAdditionalInfo, typeImage, markerImage, numSpots, type, date.getTimeInMillis());
    }
    public ScrimArea() {}

    public ScrimArea(String title, String additionalInfo, String type, int numSpots,
                     int typeImage, int markerImage, double latitude, double longitude,
                     String id, long date) {
        this.title = title;
        this.additionalInfo = additionalInfo;
        this.type = type;
        this.numSpots = numSpots;
        this.typeImage = typeImage;
        this.markerImage = markerImage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
        this.date = date;
        this.center = new LatLng(latitude, longitude);
    }

    public void populateDateText(TextView textView) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(getDate());
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
        // VitalizeApplication.removeAreaPassTimeLimit();
        for(ScrimArea area: VitalizeApplication.getAllAreas()) {
            area.scrimMarker = map.addMarker(new MarkerOptions().position(area.getCenter()).title(area.getTitle()).draggable(true).snippet(area.getAdditionalInfo())
                .icon(BitmapDescriptorFactory.fromResource(area.getMarkerImage())));
        }
    }

    public void update(String title, String theAdditionalInfo, int typeImage, int markerImage, int numSpots, String type, long date) {
        this.title = title;
        this.additionalInfo = theAdditionalInfo;
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

    public void setScrimMarker(Marker scrimMarker) {
        this.scrimMarker = scrimMarker;
    }

    public static ScrimArea getScrimAreaOfMarker(Marker toSearchFor, List<ScrimArea> scrimAreaList) {
        for(int k=0; k<scrimAreaList.size(); k++) {
            //marker comparison won't work, creating new ones in memory
            if(scrimAreaList.get(k).getCenter().equals(toSearchFor.getPosition())){
                return scrimAreaList.get(k);
            }
        }
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public String getType() {
        return type;
    }

    public int getNumSpots() {
        return numSpots;
    }

    public int getTypeImage() {
        return typeImage;
    }

    public int getMarkerImage() {
        return markerImage;
    }

    public String getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public LatLng getCenter() {
        if (center == null) {
            center = new LatLng(latitude, longitude);
        }
        return center;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Area{" +
                "title='" + title + '\'' +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", type='" + type + '\'' +
                ", numSpots=" + numSpots +
                ", typeImage=" + typeImage +
                ", markerImage=" + markerImage +
                ", center=" + center +
                ", id=" + id +
                ", date=" + date +
                '}';
    }

}
