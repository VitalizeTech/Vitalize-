package com.chris.scrim;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.support.design.widget.NavigationView;

import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;
import SlidingMenu.SlidingMenu;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = MapsActivity.class.getName();
    private GoogleMap mMap;
    private List<ScrimArea> myAreas;
    private SlidingMenu mySlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myAreas = new ArrayList<>();


        // configure the SlidingMenu
        mySlidingMenu = new SlidingMenu(this);
        mySlidingMenu.setMode(SlidingMenu.LEFT);
        mySlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        mySlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        // Need to add shadow.
        // mySlidingMenu.setShadowDrawable(R.drawable.common_plus_signin_btn_icon_dark);
        mySlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mySlidingMenu.setFadeDegree(0.35f);
        mySlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        mySlidingMenu.setMenu(R.layout.sliding_menu);
        ((NavigationView) mySlidingMenu.getMenu().findViewById(R.id.nav_view))
                .setNavigationItemSelectedListener(this);
    }

    private ScrimArea getScrimArea(List<ScrimArea> scrimAreas, LatLng pointOfInterest) {

        for (ScrimArea a : scrimAreas) {
            if (a.showIfInCircle(pointOfInterest)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Replace the (default) location source of the my-location layer with our custom LocationSource
        //mMap.setLocationSource(followMeLocationListener);

        // Set default zoom
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
        //followMeLocationListener = new FollowMeLocationListener(this, googleMap);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                ScrimArea scrimA = getScrimArea(myAreas, latLng);
                if (scrimA == null) {
                    //inflate layout we want
                    final View view = MapsActivity.this.getLayoutInflater().inflate(R.layout.new_scrim_area, null);
                    // ask the alert dialog to use our layout
                    //prompt for dialog
                    //show a dialog that prompts the user if he/she wants to delete
                    AlertDialog.Builder addBuild = new AlertDialog.Builder(MapsActivity.this)
                            .setTitle(getResources().getString(R.string.new_scrim_title))
                            .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //retrieve our data
                                    String title = ((EditText) view.findViewById(R.id.editText)).getText().toString();
                                    String description = ((EditText) view.findViewById(R.id.editText2)).getText().toString();
                                    myAreas.add(new ScrimArea(mMap, latLng, title, description));
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert);
                    addBuild.setView(view);
                    addBuild.show();
                } else {
                    scrimA.showMarkerMessage();
                }

            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //find which scrim area corresponds to marker
                final ScrimArea temp = getScrimArea(myAreas, marker.getPosition());
                //show a dialog that prompts the user if he/she wants to delete
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                temp.remove();
                                myAreas.remove(temp);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });
    }


    private void moveToCurrentLocation(LatLng currentLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()) {
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
}
