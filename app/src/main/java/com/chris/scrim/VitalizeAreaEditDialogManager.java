package com.chris.scrim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by chris on 2/21/2016.
 */
public class VitalizeAreaEditDialogManager {
    private static final String TAG = VitalizeAreaEditDialogManager.class.getName();
    private static final int MIN_TITLE_LENGTH = 6;
    private static final int MAX_DAYS = 3;
    private static final int NUM_AM_HOURS = 12;
    private static final int ALLOWED_DIFF = -5000;
    private Activity mActivity;
    private GoogleMap mMap;
    private DBFireBaseHelper firebaseDBHelper;
    private Context mContext;

    public VitalizeAreaEditDialogManager(Activity theActivity, GoogleMap theMap, Context context) {
        mActivity = theActivity;
        mMap = theMap;
        firebaseDBHelper = new DBFireBaseHelper(theActivity);
        mContext = context;
    }

    public void showEditScrimDialog(final ScrimArea theAre, final LatLng latLng ) {
        //inflate layout we wantz
        InputStream inputstream = mContext.getResources().openRawResource(R.raw.pokemon);
        CSVReader csvFile = new CSVReader(inputstream);
        List pokemonList = csvFile.read();
        // Create adapter here
        // AutoCompleteTextview textview = (autocompletetextview) findViewById(R.id.blah);
        // textView.setAdapter(adapter);
        final View rightView = mActivity.getLayoutInflater().inflate(R.layout.new_scrim_area, null);
        Calendar calendar = Calendar.getInstance();
        if(theAre != null) {
            calendar.setTimeInMillis(theAre.getDate());
        }
        Button cancelButton = (Button) rightView.findViewById(R.id.cancelBtn);
        Button createButton = (Button) rightView.findViewById(R.id.createBtn);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, VitalizeApplication.getTypes());

        // ask the alert dialog to use our layout
        //prompt for dialog
        //show a dialog that prompts the user if he/she wants to delete
        AlertDialog.Builder addBuild = new AlertDialog.Builder(mActivity);
        addBuild.setView(rightView);
        final AlertDialog alertDialog = addBuild.create();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if (theAre != null) {
            createButton.setText("Save");
        }
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText) rightView.findViewById(R.id.titleEdit)).getText().toString();
                String combatPower = ((EditText) rightView.findViewById(R.id.combatPowerEdit)).getText().toString();
                if (title.length() < MIN_TITLE_LENGTH) {
                    new AlertDialog.Builder(mActivity).setMessage("Pokemon name must be at least 6 characters").setPositiveButton(
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    ).show();
                } else if (combatPower.isEmpty()) {
                    new AlertDialog.Builder(mActivity).setMessage("You must enter number of people.").setPositiveButton(
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    ).show();
                } else {
                    int cp = Integer.parseInt(combatPower);
                    int playerLevel = Integer.valueOf(((EditText) rightView.
                            findViewById(R.id.editPlayerLvl)).getText().toString());
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    if (theAre == null) {
                        ScrimArea newArea = new ScrimArea(mMap, latLng, title, cp, playerLevel, currentTime);
                        newArea.setCreator(VitalizeApplication.currentUser.username);
                        VitalizeApplication.getAllAreas().add(newArea);
                        // Add to firebase
                        firebaseDBHelper.insertScrimAreaInFirebase(newArea, VitalizeApplication.currentUser.id);
                        // end firebase
                    } else {
                        theAre.update(title, cp, playerLevel, currentTime);
                        firebaseDBHelper.updateScrimAreaInFireBase(theAre);
                    }
                    alertDialog.dismiss();
                }
            }
        });
        if (theAre != null) {
            ((EditText) rightView.findViewById(R.id.titleEdit)).setText(theAre.getTitle());
            ((EditText) rightView.findViewById(R.id.combatPowerEdit)).setText(String.valueOf(theAre.getCombatPower()));
            ((EditText) rightView.findViewById(R.id.editPlayerLvl)).setText(theAre.getPlayerLevel());
        }
        alertDialog.show();
    }
    private void setText(TextView timeDisplay, int minute, int month, int dayOfMonth, Calendar getHour) {
        //getHour - 16 -> 4
        String minuteText;
        if (minute >= 10) {
            minuteText = "" + minute;
        } else {
            minuteText = "0" + minute;
        }
        int hourIn12HourClock = getHour.get(Calendar.HOUR);
        if(hourIn12HourClock == 0) {
            hourIn12HourClock = 12;
        }
        if (getHour.get(Calendar.HOUR_OF_DAY) >= NUM_AM_HOURS) {
            timeDisplay.setText(month + "/" + dayOfMonth + " " + hourIn12HourClock + ":" + minuteText + "PM");
        } else {
            timeDisplay.setText(month + "/" + dayOfMonth + " " + hourIn12HourClock + ":" + minuteText + "AM");
        }
    }
    public void setDeleteClickListener (Button delete, final Marker marker, final AlertDialog markerInfoDialog) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show a dialog that prompts the user if he/she wants to delete
                new AlertDialog.Builder(mActivity)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                List<ScrimArea> allAreas = VitalizeApplication.getAllAreas();
                                ScrimArea toRemove = ScrimArea.getScrimAreaOfMarker(marker, allAreas);
                                //remove it from point of truth as well
                                allAreas.remove(toRemove);
                                // continue with delete
                                marker.remove();
                                firebaseDBHelper.removeScrimAreaInFirebase(toRemove.getId());
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
}
