package com.chris.scrim;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

/**
 * Created by chris on 2/21/2016.
 */
public class VitalizeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
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
}
