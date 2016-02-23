package com.chris.scrim;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chris on 2/21/2016.
 */
public class VitalizeApplication extends Application {
    private DBHelper dbHelper;
    private static Map<String, Integer> typeToMarkerImage;
    private static Map<String, Integer> typeToTypeImage;
    private static String[] types;
    private static List<ScrimArea> allAreas;
    public static List<ScrimArea> getAllAreas() {
        return allAreas;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        initializeMaps();
        allAreas = new ArrayList<>();
        dbHelper = new DBHelper(this);
        allAreas = dbHelpler.getAllScrimAreas();
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
    private static void initializeMaps() {
        String[] types = {"Basketball", "Football", "Frisbee", "Soccer", "Tennnis", "Volleyball"};

    }
}
