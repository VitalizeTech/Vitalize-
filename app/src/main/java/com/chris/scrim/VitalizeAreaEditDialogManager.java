package com.chris.scrim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Calendar;
import java.util.List;

/**
 * Created by chris on 2/21/2016.
 */
public class VitalizeAreaEditDialogManager {
    private static final int NUM_AM_HOURS = 12;
    private Activity mActivity;
    private GoogleMap mMap;
    private List<ScrimArea> myAreas;
    private final int[] markerImages = {R.drawable.basketball_marker, R.drawable.football_marker, R.drawable.frisbee_marker,
            R.drawable.soccer_marker, R.drawable.tennis_marker, R.drawable.volleyball_marker};
    private final int[] typeImages = {R.drawable.basketball, R.drawable.football,
            R.drawable.frisbee, R.drawable.soccer, R.drawable.tennis,
            R.drawable.volleyball};
    public VitalizeAreaEditDialogManager(Activity theActivity, GoogleMap theMap, List<ScrimArea> theAreas) {
        mActivity = theActivity;
        mMap = theMap;
        myAreas = theAreas;
    }
    public void showEditScrimDialog(final ScrimArea theAre, final LatLng latLng ) {
        //inflate layout we wantz
        final View rightView = mActivity.getLayoutInflater().inflate(R.layout.new_scrim_area, null);
        final Button timePickerButton = (Button) rightView.findViewById(R.id.pickStartTime);
        final TextView timeDisplay = (TextView) rightView.findViewById(R.id.startDisplay);
        setTimeButtonClickListener(timePickerButton, timeDisplay);
        final Spinner typeSpinner = (Spinner) rightView.findViewById(R.id.typeSpinner);
        String[] types = {"Basketball", "Football", "Frisbee", "Soccer", "Tennnis", "Volleyball"};
        Button cancelButton = (Button) rightView.findViewById(R.id.cancelBtn);
        Button createButton = (Button) rightView.findViewById(R.id.createBtn);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(mActivity,
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
        AlertDialog.Builder addBuild = new AlertDialog.Builder(mActivity);
        addBuild.setView(rightView);
        final AlertDialog alertDialog = addBuild.create();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if(theAre != null) {
            createButton.setText("Save");
        }
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
                if(theAre == null) {
                    myAreas.add(new ScrimArea(mMap, latLng, title, description,
                            markerImages[typeSpinner.getSelectedItemPosition()],
                            typeImages[typeSpinner.getSelectedItemPosition()], numSpot, type));
                } else {
                    theAre.update(title, description,  typeImages[typeSpinner.getSelectedItemPosition()],
                            markerImages[typeSpinner.getSelectedItemPosition()], numSpot, type);
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    public void setTimeButtonClickListener(Button timePickerButton, final TextView timeDisplay) {
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get current time
                final Calendar currentTime = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, final int monthOfYear, final int dayOfMonth) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //is am or pm
                                //so 16 is represented as 4
                                currentTime.set(Calendar.HOUR, hourOfDay);
                                currentTime.set(Calendar.MINUTE, minute);
                                int month = monthOfYear + 1;
                                String minuteText;
                                if (minute >= 10) {
                                    minuteText = "" + minute;
                                } else {
                                    minuteText = "0" + minute;
                                }
                                if (hourOfDay > NUM_AM_HOURS) {
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
}
