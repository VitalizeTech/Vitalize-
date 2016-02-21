package com.chris.scrim;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.support.design.widget.NavigationView;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import SlidingMenu.SlidingMenu;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
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

    private ScrimArea getScrimAreaOfMarker(Marker markSearchFor) {
        for(int k=0; k<myAreas.size(); k++) {
            if(myAreas.get(k).getScrimMarker().equals(markSearchFor)){
                return myAreas.get(k);
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

                ScrimArea markerScrim = getScrimAreaOfMarker(marker);
                typeImage.setImageResource(markerScrim.getTypeImage());
                spotsLeft.setText("1/" + markerScrim.getNumSpots());
                type.setText(markerScrim.getType());
                AlertDialog markerInfoDialog = markerInfoDialogBuilder.create();
                final Button delete = (Button) markerInfoView.findViewById(R.id.deleteButton);
                setDeleteClickListener(delete, marker, markerInfoDialog);
                markerInfoDialog.setView(markerInfoView);
                markerInfoDialog.getWindow().getAttributes().y = -600;
                markerInfoDialog.show();
                //find which scrim area corresponds to marker
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
    private void setOnMapClickListener (final GoogleMap mMap) {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                final int[] markerImages = {R.drawable.basketball_marker, R.drawable.football_marker, R.drawable.frisbee_marker,
                        R.drawable.soccer_marker, R.drawable.tennis_marker, R.drawable.volleyball_marker};
                //inflate layout we want
                final View rightView = MapsActivity.this.getLayoutInflater().inflate(R.layout.new_scrim_area, null);
                final Button timePickerButton = (Button) rightView.findViewById(R.id.pickStartTime);
                final TextView timeDisplay = (TextView) rightView.findViewById(R.id.startDisplay);
                setTimeButtonClickListener(timePickerButton, timeDisplay);
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
                        String title = ((EditText) rightView.findViewById(R.id.titleEdit)).getText().toString();
                        String description = ((EditText) rightView.findViewById(R.id.editAdditInfo)).
                                getText().toString();
                        String type = (String) ((Spinner) rightView.
                                findViewById(R.id.typeSpinner)).getSelectedItem();
                        int numSpot = Integer.valueOf(((EditText) rightView.
                                findViewById(R.id.editPpl)).getText().toString());
                        myAreas.add(new ScrimArea(mMap, latLng, title, description,
                                markerImages[typeSpinner.getSelectedItemPosition()],
                                typeImages[typeSpinner.getSelectedItemPosition()], numSpot, type));
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }
    private void setTimeButtonClickListener(Button timePickerButton, final TextView timeDisplay) {
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get current time
                final Calendar currentTime = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(MapsActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, final int monthOfYear, final int dayOfMonth) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(MapsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //is am or pm
                                currentTime.set(Calendar.HOUR, hourOfDay);
                                currentTime.set(Calendar.MINUTE, minute);

                                boolean isPm = currentTime.get(Calendar.AM_PM) == Calendar.PM;
                                int month = monthOfYear + 1;
                                String minuteText;
                                if (minute >= 10) {
                                    minuteText = "" + minute;
                                } else {
                                    minuteText = "0" + minute;
                                }
                                if (isPm) {
                                    timeDisplay.setText(month + "/" + dayOfMonth + " " + currentTime.get(Calendar.HOUR) + ":" + minuteText + "PM");
                                } else {
                                    timeDisplay.setText(month + "/" + dayOfMonth + " " + currentTime.get(Calendar.HOUR) + ":" + minuteText + "AM");
                                }
                            }
                        }, currentTime.get(Calendar.HOUR), currentTime.get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                    }
                }, currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });
    }
    private void setDeleteClickListener (Button delete, final Marker marker, final AlertDialog markerInfoDialog) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show a dialog that prompts the user if he/she wants to delete
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                marker.remove();
                                markerInfoDialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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
