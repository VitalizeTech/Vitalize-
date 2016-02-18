package com.chris.scrim;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.support.design.widget.NavigationView;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

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

    @Override
    protected void onResume() {
        super.onResume();
        if(mMap != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /* Disable the my-location layer (this causes our LocationSource to be automatically deactivated.) */
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
        }
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
        new FollowMeLocationListener(this, googleMap);
        //mMap.setLocationSource(followMeLocationListener);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                final int[] markerImages = {R.drawable.basketball_marker, R.drawable.football_marker, R.drawable.frisbee_marker,
                        R.drawable.soccer_marker, R.drawable.tennis_marker, R.drawable.volleyball_marker};
                //inflate layout we want
                final View rightView = MapsActivity.this.getLayoutInflater().inflate(R.layout.new_scrim_area, null);
                final Spinner typeSpinner = (Spinner) rightView.findViewById(R.id.typeSpinner);
                String[] types = {"Basketball", "Football", "Frisbee", "Soccer", "Tennnis", "Volleyball"};
                final int[] typeImages = {R.drawable.basketball, R.drawable.football,
                        R.drawable.frisbee, R.drawable.soccer, R.drawable.tennis,
                        R.drawable.volleyball};
                Button cancelButton = (Button) rightView.findViewById(R.id.cancelBtn);
                Button createButton = (Button) rightView.findViewById(R.id.createBtn);
                ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(MapsActivity.this,
                        android.R.layout.simple_spinner_item, types);
                typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(typeAdapter);
                typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ImageView imageForType = (ImageView) rightView.findViewById(R.id.imageForType);
                        imageForType.setImageResource(typeImages[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                // ask the alert dialog to use our layout
                //prompt for dialog
                //show a dialog that prompts the user if he/she wants to delete
                AlertDialog.Builder addBuild = new AlertDialog.Builder(MapsActivity.this);
                addBuild.setView(rightView);
                final AlertDialog alertDialog = addBuild.create();
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = ((EditText) rightView.findViewById(R.id.editPpl)).getText().toString();
                        String description = ((EditText) rightView.findViewById(R.id.titleEdit)).getText().toString();
                        myAreas.add(new ScrimArea(mMap, latLng, title, description,
                                markerImages[typeSpinner.getSelectedItemPosition()]));
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //find which scrim area corresponds to marker
                //show a dialog that prompts the user if he/she wants to delete
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                marker.remove();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                marker.showInfoWindow();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });
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
