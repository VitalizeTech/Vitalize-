package com.chris.scrim;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.client.Firebase;

import SlidingMenu.SlidingMenu;

/**
 * Created by chris on 2/21/2016.
 */
public class VitalizeSlidingMenu {
    public static final String TAG = "Vitalize";
    private static SlidingMenu vitalizeSlidingMenu;
    private static NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    public static void initializeSlidingMenu(Activity theActivity) {
        initializeOnNavItemSelectedListener(theActivity);
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

    private static void initializeOnNavItemSelectedListener(final Activity theActivity) {
        onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profile:
                        Log.d(TAG, "Profile");
                        Intent profileIntent = new Intent(theActivity, Profile.class);
                        theActivity.startActivity(profileIntent);
                        break;
//                    case R.id.settings:
//                        Log.d(TAG, "Settings");
//                        break;
//                    case R.id.subscription:
//                        Log.d(TAG, "Settings");
//                        break;
                    case R.id.about:
                        Log.d(TAG, "About");
                        Intent aboutUsIntent = new Intent(theActivity, AboutUs.class);
                        theActivity.startActivity(aboutUsIntent);
                        break;
                    case R.id.logout:
                        Log.d(TAG, "Log Out");
                        Firebase ref = new Firebase("https://scrim.firebaseio.com/");
                        ref.unauth();
                        Toast.makeText(theActivity, "Logged Out", Toast.LENGTH_SHORT).show();
                        Intent logoutIntent = new Intent(theActivity, LoginActivity.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // TODO: Fix back button after logout
                        theActivity.startActivity(logoutIntent);
                        theActivity.finish();
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
