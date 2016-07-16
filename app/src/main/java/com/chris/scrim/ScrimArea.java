package com.chris.scrim;


import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chris on 2/4/2016.
 */
@JsonIgnoreProperties(value = {"scrimMarker", "center", "members"}, ignoreUnknown = true)
public class ScrimArea {
    private static final String TAG = ScrimArea.class.getName();
    private Marker scrimMarker;
    private String title;
    private String creator;

    private LatLng center;
    private double latitude;
    private double longitude;
    private String id;
    private long date;
    private int combatPower;
    private int playerLevel;
    public ScrimArea(GoogleMap mMap, LatLng  center, String theName, int combatPower, int playerLevel, long caughtTime){
        scrimMarker = mMap.addMarker(new MarkerOptions().position(center).title(getTitle()).
                draggable(true).snippet(theName));
        this.center  = center;
        this.latitude = center.latitude;
        this.longitude = center.longitude;
        update(theName, combatPower, playerLevel, caughtTime);
    }


    public void update(String title, int combatPower, int playerLevel, long date) {
        this.title = title;
        if(scrimMarker != null) {
            scrimMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.abra));
        }
        this.combatPower = combatPower;
        this.playerLevel = playerLevel;
        this.date = date;
    }

    public void setPokemon(String pokemon){
        this.title = title;
        //markerImage = VitalizeApplication.getMarkerImage(type);
        //typeImage = VitalizeApplication.getTypeImage(type);
    }
    public ScrimArea() {

    }

    public void populateDateText(TextView textView) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(getDate());
        SimpleDateFormat format = new SimpleDateFormat("M/d h:mm a");
        textView.setText(format.format(date.getTime()));
    }

    public static void loadAllAreasOntoMap(GoogleMap map) {
        map.clear();
        // VitalizeApplication.removeAreaPassTimeLimit();
        for(ScrimArea area: VitalizeApplication.getAllAreas()) {
                area.scrimMarker = map.addMarker(new MarkerOptions().position(area.getCenter()).title(area.getTitle()).draggable(true).snippet(area.getTitle())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.abra)));
        }
    }

    public Marker getScrimMarker () {
        return scrimMarker;
    }

    public static Calendar parseDateOut(String date) {
        Calendar parsedCalendarOut = Calendar.getInstance();
        String[] timeComponents = date.split(" ");
        String dateComponent = timeComponents[0];
        String[] dayAndMonth = dateComponent.split("/");
        parsedCalendarOut.set(Calendar.MONTH, (Integer.parseInt(dayAndMonth[0])-1));
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

    public String getId() {
        return id;
    }

    public int getCombatPower() {
        return combatPower;
    }

    public int getPlayerLevel() {
        return playerLevel;
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
                ", center=" + center +
                ", id=" + id +
                ", date=" + date +
                '}';
    }
    public void setCenter(LatLng center) {
        this.center = center;
        longitude = center.longitude;
        latitude = center.latitude;
    }

    //TODO: error checking?
    public String getCreator(){return creator;}
    public void setCreator(String _c){this.creator = _c;}
}
