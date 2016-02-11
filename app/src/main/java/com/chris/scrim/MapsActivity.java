package com.chris.scrim;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import SlidingMenu.SlidingMenu;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {

    private final static String TAG = MapsActivity.class.getName();
    private GoogleMap mMap;
    private SlidingMenu mySlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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
        LatLng startLocation = new LatLng(47.5626337, -122.1344056);
        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(startLocation).title("Pickup").draggable(true).snippet("For Group")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        moveToCurrentLocation(startLocation);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("new").draggable(true).snippet("New bubble")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
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
