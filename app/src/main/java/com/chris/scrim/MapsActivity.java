package com.chris.scrim;

import android.Manifest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;



public class MapsActivity extends TouchActivity implements OnMapReadyCallback, Observer {
    private static final int PLACE_PICKER_REQUEST = 1;
    private GoogleMap mMap;
    private VitalizeAreaEditDialogManager vitalizeAreaEditDialogManager;
     //temp filter -- because you don't need to remember your filter choice in firebase
    private List<Integer> filterChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        filterChoice = new ArrayList<>();
        setUpActionBar();

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
            if(mMap != null) {
                mMap.setMyLocationEnabled(false);
            }
        }
    }

    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        vitalizeAreaEditDialogManager =  new VitalizeAreaEditDialogManager(this, googleMap, this);
        mMap = googleMap;
        final DBFireBaseHelper firebaseDBHelper = new DBFireBaseHelper(this);

        // Get all areas and put it on the map when it is done loading.
        firebaseDBHelper.getAllScrimAreasFromFirebase();

        // Replace the (default) location source of the my-location layer with our custom LocationSource
        new FollowMeLocationListener(this, googleMap);
        setOnMapClickListener(mMap);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        ////Default to seattle when map is loaded
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(47.6097, -122.3331), 15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        //Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);
        ////
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        final ScrimArea markerScrim = ScrimArea.getScrimAreaOfMarker(marker, VitalizeApplication.getAllAreas());
                        if (markerScrim != null) {
                            AlertDialog.Builder markerInfoDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                            final View markerInfoView = MapsActivity.this.getLayoutInflater().inflate(R.layout.marker_info, null);
                            markerScrim.populateDateText((TextView) markerInfoView.findViewById(R.id.dateText));
                            //final Button
                            final ImageView typeImage = (ImageView) markerInfoView.findViewById(R.id.typeImage);
                            ((TextView) markerInfoView.findViewById(R.id.titleText)).setText(markerScrim.getTitle());
                            ((TextView) markerInfoView.findViewById(R.id.combatPowerTxt)).setText("  Combat Power: " + markerScrim.getCombatPower()+"");
                            ((TextView) markerInfoView.findViewById(R.id.playerLvlTxt)).setText("  Player Level: " + markerScrim.getPlayerLevel()+"");
                            ((TextView) markerInfoView.findViewById(R.id.createdBy)).setText("Reported By:" + markerScrim.getCreator());
                            typeImage.setImageResource(R.drawable.abra);
                            final AlertDialog markerInfoDialog = markerInfoDialogBuilder.create();
                            final Button delete = (Button) markerInfoView.findViewById(R.id.deleteButton);
                            View isReporterOptions = markerInfoView.findViewById(R.id.isUserOptions);
                            CheckBox favorite = (CheckBox) markerInfoView.findViewById(R.id.Favorite);
                            if (VitalizeApplication.currentUser.getFavoriteList().contains(markerScrim.getId())) {
                                favorite.setChecked(true);
                            }
                            favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        VitalizeApplication.currentUser.addFavorite(markerScrim);
                                    } else {
                                        VitalizeApplication.currentUser.deleteFavorite(markerScrim);
                                    }
                                }
                            });
                            //if current users is the creator of the even
                            if (markerScrim.getCreator().equals(VitalizeApplication.currentUser.username)) {
                                isReporterOptions.setVisibility(View.VISIBLE);
                            } else {
                                isReporterOptions.setVisibility(View.INVISIBLE);
                            }


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
                        }
                        return true;
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
    public void setUpActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        // actionBar.setIcon(R.drawable.ic_action_search);

        LayoutInflater inflator = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_action_bar, null);
        Button filterButton = (Button)v.findViewById(R.id.filterButton);
        Button searchButton = (Button)v.findViewById(R.id.searchButton);
        Button slidingMenuButton = (Button)v.findViewById(R.id.menuButton);
        setTouchNClick(searchButton);
        setTouchNClick(slidingMenuButton);
        setTouchNClick(filterButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e) {
                } catch (GooglePlayServicesRepairableException e) {
                }
            }
        });
        slidingMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VitalizeSlidingMenu.show();
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View filterView = MapsActivity.this.getLayoutInflater().inflate(R.layout.filter, null);
                    final Button filterButton = (Button) filterView.findViewById(R.id.Filter_confirm);
                    Button cancelButton = (Button) filterView.findViewById(R.id.Filter_cancel);
                    final int [] CheckId = {R.id.bballCheckBox, R.id.fballCheckBox,
                            R.id.FrisbeeCheckBox, R.id.soccerCheckBox, R.id.tennisCheckBox, R.id.vballCheckBox};
                    for (int c = 0; c < CheckId.length; c++) {
                        CheckBox temp = (CheckBox) filterView.findViewById(CheckId[c]);
                        temp.setChecked(filterChoice.contains(CheckId[c]));
                    }
                    final CheckBox myfavorite = (CheckBox)filterView.findViewById(R.id.myFavorite);
                    myfavorite.setChecked(filterChoice.contains(R.id.myFavorite));
                    //show a dialog that prompts the user if he/she wants to delete
                    AlertDialog.Builder addBuild = new AlertDialog.Builder(MapsActivity.this);
                    addBuild.setView(filterView);
                    final AlertDialog alertDialog = addBuild.create();
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            VitalizeApplication.getAllAreas();
                            alertDialog.dismiss();
                        }
                    });

                    filterButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final List<String> selectedTypes = new ArrayList<>();
                            for (int c = 0; c < CheckId.length; c++) {
                                CheckBox temp = (CheckBox) filterView.findViewById(CheckId[c]);
                                if (temp.isChecked()) {
                                    if (!filterChoice.contains(CheckId[c])) {
                                        filterChoice.add(CheckId[c]);
                                    }
                                    selectedTypes.add(temp.getText().toString());
                                } else {
                                    if (filterChoice.contains(CheckId[c])) {
                                        int i = filterChoice.indexOf(CheckId[c]);
                                        filterChoice.remove(i);
                                    }
                                }
                            }
                            if (myfavorite.isChecked()) {
                                if (!filterChoice.contains((R.id.myFavorite))) {
                                        filterChoice.add(R.id.myFavorite);
                                }
                                if (selectedTypes.isEmpty()) {
                                    for (ScrimArea a : VitalizeApplication.getAllAreas()) {
                                        a.getScrimMarker().setVisible(VitalizeApplication.currentUser.getFavoriteList().contains(a.getId()));
                                    }
                                } else {
                                    for (ScrimArea a : VitalizeApplication.getAllAreas()) {
                                        a.getScrimMarker().setVisible(selectedTypes.contains(a.getTitle()) &&
                                                VitalizeApplication.currentUser.getFavoriteList().contains(a.getId()));
                                    }
                                }
                            } else {
                                //filter the item and just display the option chosen
                                if (filterChoice.contains(R.id.myFavorite)) {
                                    int i = filterChoice.indexOf(R.id.myFavorite);
                                    filterChoice.remove(i);
                                }
                                if (selectedTypes.isEmpty()) {
                                    for (ScrimArea a : VitalizeApplication.getAllAreas()) {
                                        a.getScrimMarker().setVisible(true);
                                    }
                                } else {
                                    for (ScrimArea a : VitalizeApplication.getAllAreas()) {
                                        a.getScrimMarker().setVisible(selectedTypes.contains(a.getTitle()));
                                    }
                                }
                            }
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
        });
        actionBar.setCustomView(v);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                FollowMeLocationListener.moveToCurrentLocation(mMap, place.getLatLng());
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()+"").
                        draggable(false));
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        ScrimArea.loadAllAreasOntoMap(mMap);
    }


}
