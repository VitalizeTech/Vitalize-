package com.chris.scrim;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<ScrimArea> myAreas;
    private VitalizeAreaEditDialogManager vitalizeAreaEditDialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myAreas = new ArrayList<>();
        VitalizeSlidingMenu.initializeSlidingMenu(this);
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
        vitalizeAreaEditDialogManager =  new VitalizeAreaEditDialogManager(this, googleMap, myAreas);
        mMap = googleMap;
        // Replace the (default) location source of the my-location layer with our custom LocationSource
        new FollowMeLocationListener(this, googleMap);
        //mMap.setLocationSource(followMeLocationListener);
       setOnMapClickListener(mMap);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                AlertDialog.Builder markerInfoDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                final View markerInfoView = MapsActivity.this.getLayoutInflater().inflate(R.layout.marker_info, null);
                //final Button
                final ImageView typeImage = (ImageView) markerInfoView.findViewById(R.id.typeImage);
                TextView spotsLeft = (TextView) markerInfoView.findViewById(R.id.spotsLeft);
                TextView type = (TextView) markerInfoView.findViewById(R.id.typeText);
                final ScrimArea markerScrim = ScrimArea.getScrimAreaOfMarker(marker, myAreas);
                ((TextView)markerInfoView.findViewById(R.id.titleText)).setText(markerScrim.getTitle());
                ((TextView)markerInfoView.findViewById(R.id.additInfoText)).setText(markerScrim.getAdditionalInfo());
                typeImage.setImageResource(markerScrim.getTypeImage());
                spotsLeft.setText("1/" + markerScrim.getNumSpots());
                type.setText(markerScrim.getType());
                final AlertDialog markerInfoDialog = markerInfoDialogBuilder.create();
                final Button delete = (Button) markerInfoView.findViewById(R.id.deleteButton);
                vitalizeAreaEditDialogManager.setDeleteClickListener(delete, marker, markerInfoDialog);
                markerInfoView.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        markerInfoDialog.dismiss();
                        vitalizeAreaEditDialogManager.showEditScrimDialog(markerScrim, null);
                    }
                });
                markerInfoDialog.setView(markerInfoView);
                markerInfoDialog.getWindow().getAttributes().y = -600;
                markerInfoDialog.show();
                //find which scrim area corresponds to marker
                return false;
            }
        });
    }

    private void setOnMapClickListener (final GoogleMap mMap) {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                //inflate layout we wantz
                vitalizeAreaEditDialogManager.showEditScrimDialog(null, latLng);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.find ) {
            AlertDialog.Builder findGroupDialogBuilder = new AlertDialog.Builder(MapsActivity.this, R.style.AppTheme);
            LayoutInflater inflateDialogLayout = (LayoutInflater) MapsActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View findGroupView = inflateDialogLayout.inflate(R.layout.find_scrim_dialog, null, false);
            findGroupDialogBuilder.setView(findGroupView);
            final AlertDialog findGroupDialog = findGroupDialogBuilder.create();
            findGroupView.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findGroupDialog.dismiss();
                }
            });
            Window window = findGroupDialog.getWindow();
            window.getAttributes().y = -1075;
            window.setBackgroundDrawableResource(R.color.white);
            window.setLayout(1500, 200);
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.75f;
            windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            findGroupDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
