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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by chris on 2/21/2016.
 */
public class VitalizeAreaEditDialogManager {
    private static final int MIN_TITLE_LENGTH = 6;
    private static final int NUM_AM_HOURS = 12;
    private Activity mActivity;
    private GoogleMap mMap;
    private DBHelper dbHelper;

    public VitalizeAreaEditDialogManager(Activity theActivity, GoogleMap theMap) {
        mActivity = theActivity;
        mMap = theMap;
        dbHelper = new DBHelper(theActivity);
    }
    public void showEditScrimDialog(final ScrimArea theAre, final LatLng latLng ) {
        //inflate layout we wantz
        final View rightView = mActivity.getLayoutInflater().inflate(R.layout.new_scrim_area, null);
        Calendar current = theAre == null? Calendar.getInstance():theAre.getDate();

        setText((TextView) rightView.findViewById(R.id.startDisplay), current.get(Calendar.MINUTE),
                current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH), current);

        final Button timePickerButton = (Button) rightView.findViewById(R.id.pickStartTime);
        final TextView timeDisplay = (TextView) rightView.findViewById(R.id.startDisplay);
        setTimeButtonClickListener(timePickerButton, timeDisplay);


        final Spinner typeSpinner = (Spinner) rightView.findViewById(R.id.typeSpinner);
        Button cancelButton = (Button) rightView.findViewById(R.id.cancelBtn);
        Button createButton = (Button) rightView.findViewById(R.id.createBtn);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item, VitalizeApplication.getTypes());

        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageForType = (ImageView) rightView.findViewById(R.id.imageForType);
                imageForType.setImageResource(VitalizeApplication.getTypeImage(VitalizeApplication.getTypes()[position]));
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
        if (theAre != null) {
            createButton.setText("Save");
        }
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = ((EditText) rightView.findViewById(R.id.titleEdit)).getText().toString();
                if (title.length() < MIN_TITLE_LENGTH) {
                    new AlertDialog.Builder(mActivity).setMessage("Title must be at least 6 characters").setPositiveButton(
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    ).show();
                } else {
                    String date = ((TextView) rightView.findViewById(R.id.startDisplay)).getText().toString();
                    String description = ((EditText) rightView.findViewById(R.id.editAdditInfo)).getText().toString();
                    String type = (String) ((Spinner) rightView.
                            findViewById(R.id.typeSpinner)).getSelectedItem();
                    int numSpot = Integer.valueOf(((EditText) rightView.
                            findViewById(R.id.editPpl)).getText().toString());
                    if (theAre == null) {
                        ScrimArea newArea = new ScrimArea(mMap, latLng, title, description,
                                VitalizeApplication.getMarkerImage(type), VitalizeApplication.getTypeImage(type), numSpot, type,
                                ScrimArea.parseDateOut(date));
                        VitalizeApplication.getAllAreas().add(newArea);
                        dbHelper.insertScrimAreaDB(newArea.getId(), newArea.getTitle(), newArea.getAdditionalInfo(),
                                newArea.getType(), newArea.getCenter().latitude, newArea.getCenter().longitude, newArea.getNumSpots(),
                                newArea.getDate());
                    } else {
                        theAre.update(title, description, VitalizeApplication.getTypeImage(type),
                                VitalizeApplication.getMarkerImage(type), numSpot, type, ScrimArea.parseDateOut(date));
                        dbHelper.updateScrimAreaDB(theAre.getId(), title, description, type, numSpot, theAre.getDate());
                    }
                    alertDialog.dismiss();
                }
            }
        });
        if (theAre != null) {

            ((EditText) rightView.findViewById(R.id.editAdditInfo)).setText(theAre.getAdditionalInfo());
            ((Spinner) rightView.findViewById(R.id.typeSpinner)).setSelection(Arrays.
                    asList(VitalizeApplication.getTypes()).indexOf(theAre.getType()));
            ((ImageView) rightView.findViewById(R.id.imageForType)).setImageResource(VitalizeApplication.getTypeImage(theAre.getType()));
            ((EditText) rightView.findViewById(R.id.editPpl)).setText(String.valueOf(theAre.getNumSpots()));
            ((EditText) rightView.findViewById(R.id.titleEdit)).setText(theAre.getTitle());

        }
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
                                currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                currentTime.set(Calendar.MINUTE, minute);
                                int month = monthOfYear + 1;
                                setText(timeDisplay, minute, month, dayOfMonth, currentTime);
                            }
                        }, currentTime.get(Calendar.HOUR), currentTime.get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                    }
                }, currentTime.get(Calendar.YEAR), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });
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
                                dbHelper.removeScrimAreaDB(toRemove.getId());
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
