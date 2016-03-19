package com.chris.scrim;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chris on 2/21/2016.
 */
public class VitalizeApplication extends Application {

    public static User currentUser;

    //hard coded water fountains
    public static List<LatLng> waterFountatins;

    //events should be removed 1 hour after start time
    private static final int HOUR_LIMIT = 60;

    private static DBFireBaseHelper dbHelper;
    private static Map<String, Integer> typeToMarkerImage;
    private static Map<String, Integer> typeToTypeImage;
    private static  final String[] types = {"Basketball", "Football", "Frisbee", "Soccer", "Tennis", "Volleyball"};
    private static List<ScrimArea> allAreas;
    //locally


    public static List<ScrimArea> getAllAreas() {
        return allAreas;
    }
    public static void setAllAreas(List<ScrimArea> allScrimAreas) {
        allAreas = allScrimAreas;
    }
    public static String[] getTypes() {
        return types;
    }
    //Thomas logs out
    //Jim logs in

    //Thomas logs in
    //Thoas shuts device off for an hour
    //Thomas turns on app, starts using it again
    public static void intializeLocalUser(String id, String registerUsername) {
        if(registerUsername.equals("")) {
           dbHelper.retrieveUsernameAndInitialieLocalUser(id);
        } else {
            currentUser = new User(registerUsername, "", 0, VitalizeApplication.getAvatarImage(registerUsername),
                    VitalizeApplication.getAvatarImage(registerUsername));
            currentUser.setId(id);
        }
    }

    @Override
    public void onCreate() {
        initializeMaps();
        super.onCreate();
        allAreas = new ArrayList<>();
        dbHelper = new DBFireBaseHelper(this);
        allAreas = new ArrayList<>();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {
                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }
        });
    }

    public static void initWater(){
        //hard coded water fountains
        waterFountatins = new ArrayList<>();
        waterFountatins.add(new LatLng(47.56154477173652,-122.2534108739664));
        waterFountatins.add(new LatLng( 47.59226551243146,-122.31754232317921));
        waterFountatins.add(new LatLng(47.63618897771265,-122.35381774181492));
        waterFountatins.add(new LatLng(47.67072338567659, -122.3464678595544));
        waterFountatins.add(new LatLng(47.52370976871783, -122.27356240587228));
        waterFountatins.add(new LatLng(47.680045221853256,-122.32910413888548));
        waterFountatins.add(new LatLng(47.63044530129085,-122.34796529965905));
        waterFountatins.add(new LatLng(47.67772125511591,-122.39759954172814));
    }

    public static int getMarkerImage(String type) {
        Integer markerImage = typeToMarkerImage.get(type);
        return markerImage;
    }
    public static int getTypeImage(String type) {
        return typeToTypeImage.get(type);
    }
    private static void initializeMaps() {
        final int[] markerImages = {R.drawable.basketball_marker, R.drawable.football_marker, R.drawable.frisbee_marker,
                R.drawable.soccer_marker, R.drawable.tennis_marker, R.drawable.volleyball_marker};
        final int[] typeImages = {R.drawable.basketball, R.drawable.football,
                R.drawable.frisbee, R.drawable.soccer, R.drawable.tennis,
                R.drawable.volleyball};
        typeToMarkerImage = new HashMap<>();
        typeToTypeImage = new HashMap<>();
        for(int k=0; k<types.length; k++) {
            typeToMarkerImage.put(types[k], markerImages[k]);
            typeToTypeImage.put(types[k], typeImages[k]);
        }
    }
    public static int getAvatarImage(String username){
        int[] avatarImages = {R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4,
                R.drawable.avatar5};
        int avatarImage;
        if (username.isEmpty()) {
            avatarImage = avatarImages[(int)(Math.random() * avatarImages.length)];
        } else {
            avatarImage = avatarImages[(Character.toLowerCase(username.charAt(0)) - 'a') % 5];
        }
        return avatarImage;
    }
    public static void removeAreaPassTimeLimit() {
        Log.d("vitalize", String.valueOf(allAreas.size()));
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
        int i = 0;
        while(i < allAreas.size()) {
            Calendar eventExpiredTime = Calendar.getInstance();
            eventExpiredTime.setTimeInMillis(allAreas.get(i).getDate());

            eventExpiredTime.add(Calendar.MINUTE, HOUR_LIMIT);
            Calendar temp = Calendar.getInstance();
            Log.d("vital", "Delete after " + format.format(eventExpiredTime.getTime()));
            Log.d("vital", "current time " + format.format(temp.getTime()));
            if(temp.after(eventExpiredTime)) {
               // dbHelper.removeScrimAreaDB(allAreas.get(i).getId());
                allAreas.remove(i);
            } else  {
                i ++;
            }
        }
    }
}
