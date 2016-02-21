package com.chris.scrim;

import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;

import SlidingMenu.SlidingMenu;

/**
 * Created by chris on 2/21/2016.
 */
public class VitalizeSlidingMenu {
    public static final String TAG = "Vitalize";
    private static SlidingMenu vitalizeSlidingMenu;
    private static NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    public static void initializeSlidingMenu(Activity theActivity) {
        initializeOnNavItemSelectedListener();
        // configure the SlidingMenu
        vitalizeSlidingMenu = new SlidingMenu(theActivity);
        vitalizeSlidingMenu.setMode(SlidingMenu.LEFT);
        vitalizeSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        vitalizeSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        // Need to add shadow.
        // mySlidingMenu.setShadowDrawable(R.drawable.common_plus_signin_btn_icon_dark);
        vitalizeSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        vitalizeSlidingMenu.setFadeDegree(0.35f);
        vitalizeSlidingMenu.setMenu(R.layout.sliding_menu);
        vitalizeSlidingMenu.attachToActivity(theActivity, SlidingMenu.SLIDING_CONTENT);
        ((NavigationView) vitalizeSlidingMenu.getMenu().findViewById(R.id.nav_view))
                .setNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private static void initializeOnNavItemSelectedListener() {
        onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        Log.d(TAG, "Profile");
                        break;
                    case R.id.settings:
                        Log.d(TAG, "Settings");
                        break;
                    case R.id.about:
                        Log.d(TAG, "About");
                        break;
                    case R.id.home:
                    default:
                        Log.d(TAG, "Home");
                        break;
                }
                return false;
            }
        };
    }
}
