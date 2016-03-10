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

    public VitalizeAreaEditDialogManager(Activity theActivity, GoogleMap theMap) {
        mActivity = theActivity;
        mMap = theMap;
        firebaseDBHelper = new DBFireBaseHelper(theActivity);
    }
    public void showEditScrimDialog(final ScrimArea theAre, final LatLng latLng ) {
        //inflate layout we wantz
        final View rightView = mActivity.getLayoutInflater().inflate(R.layout.new_scrim_area, null);
        Calendar calendar = Calendar.getInstance();
        if(theAre != null) {
            calendar.setTimeInMillis(theAre.getDate());
        }
        setText((TextView) rightView.findViewById(R.id.startDisplay), calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar);

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
                String numPeople = ((EditText) rightView.findViewById(R.id.editPpl)).getText().toString();
                if (title.length() < MIN_TITLE_LENGTH) {
                    new AlertDialog.Builder(mActivity).setMessage("Title must be at least 6 characters").setPositiveButton(
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    ).show();
                } else if (numPeople.isEmpty()) {
                    new AlertDialog.Builder(mActivity).setMessage("You must enter number of people.").setPositiveButton(
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    ).show();
                } else {
                    int nump = Integer.parseInt(numPeople);
                    if (nump < 1) {
                        new AlertDialog.Builder(mActivity).setMessage("You need at least 1 people to create the event.").setPositiveButton(
                                "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }
                        ).show();
                    } else if (nump > 100) {
                        new AlertDialog.Builder(mActivity).setMessage("You can have no more than 100 people.").setPositiveButton(
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

                            // Add to firebase
                               firebaseDBHelper.insertScrimAreaInFirebase(newArea);
                            // end firebase
                        } else {
                            theAre.update(title, description, VitalizeApplication.getTypeImage(type),
                                    VitalizeApplication.getMarkerImage(type), numSpot, type, ScrimArea.parseDateOut(date).getTimeInMillis());
                        firebaseDBHelper.updateScrimAreaInFireBase(theAre);

                        }
                        alertDialog.dismiss();
                    }
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
                final Calendar changedTime = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                changedTime.set(Calendar.MONTH, monthOfYear);
                                changedTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                changedTime.set(Calendar.YEAR, year);
                                //is am or pm
                                //so 16 is represented as 4
                                changedTime.set(Calendar.HOUR, hourOfDay);
                                changedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                changedTime.set(Calendar.MINUTE, minute);
                                int month = monthOfYear + 1;
                                Calendar currentTime = Calendar.getInstance();
                                long diff = changedTime.getTimeInMillis() - currentTime.getTimeInMillis();
                                if(diff >= ALLOWED_DIFF && diff <= TimeUnit.MILLISECONDS.convert(MAX_DAYS, TimeUnit.DAYS)) {
                                    setText(timeDisplay, minute, month, dayOfMonth, changedTime);
                                } else {
                                    new AlertDialog.Builder(mActivity).setMessage("Must be within the next three days").setPositiveButton(
                                            "OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }
                                    ).show();
                                }
                            }
                        }, changedTime.get(Calendar.HOUR), changedTime.get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                    }
                }, changedTime.get(Calendar.YEAR), changedTime.get(Calendar.MONTH), changedTime.get(Calendar.DATE));
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
