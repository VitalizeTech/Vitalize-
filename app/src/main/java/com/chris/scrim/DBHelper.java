package com.chris.scrim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;

/**
 * Created by chris on 2/22/2016.
 */
public class DBHelper extends Observable {
    public static final String FIREBASE_LINK = "https://scrim.firebaseio.com/";
    private static final String TAG = DBHelper.class.getName();
    private static final String DATABASE_NAME = "Vitalize.db";
    private static final String TABLE_NAME = "events";
    private static final String TITLE = "title";
    private static final String ADDIT_INFO = "addit_info";
    private static final String TYPE = "type";
    private static final String LOCATION_LAT = "location_lat";
    private static final String LOCATION_LONG = "location_lon";
    private static final String NUM_SPOTS = "num_spots";
    private static final String DATE_IN_MILLIS = "date_in_millis";
    private static final String ID = "id";
    private final Firebase firebaseRef;

    public DBHelper(Context theContext) {
        Firebase.setAndroidContext(theContext);
        firebaseRef = new Firebase(FIREBASE_LINK);
        if (theContext instanceof MapsActivity) {
            addObserver((MapsActivity) theContext);
        }
    }

    public void insertScrimAreaDB2(ScrimArea newArea) {
        // Add to firebase
        // Push the new scrim area up to Firebase
        final Firebase vAreaRef = firebaseRef.child("VitalizeAreas").push();
        newArea.setId(vAreaRef.getKey());
        vAreaRef.setValue(newArea, new OnCompleteListener());
        // Add the eventId to the user
        final Firebase userAreaRef = firebaseRef.child("Users").child(firebaseRef.getAuth().getUid()).child("MyAreas");
        userAreaRef.push().setValue(vAreaRef.getKey(), new OnCompleteListener());
    }

//    public int updateScrimAreaDB(int id, String title, String additionalInfo, String type, int numSpots, Calendar date) {
//        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
//        int returnCode =  sqLiteDatabase.update(TABLE_NAME, getSharedContentUpdateInsertValues(title, additionalInfo, type, numSpots, date),
//                        ID + " = ?", new String[]{String.valueOf(id)});
//        sqLiteDatabase.close();
//        return returnCode;
//    }

    public void removeScrimAreaDB2(String vAreaFbId) {
        final Firebase vSingleAreaRef = firebaseRef.child("VitalizeAreas").child(vAreaFbId);
        vSingleAreaRef.removeValue(new OnCompleteListener());
    }

//    private ContentValues getSharedContentUpdateInsertValues(String title, String additionalInfo, String type, int numSpots, Calendar date) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(TITLE, title);
//        contentValues.put(ADDIT_INFO, additionalInfo);
//        contentValues.put(TYPE, type);
//        contentValues.put(NUM_SPOTS, numSpots);
//        contentValues.put(DATE_IN_MILLIS, date.getTimeInMillis());
//        return contentValues;
//    }


    private String checkSnapShot(DataSnapshot child) {
        String value = "";
        Object fbObj = child.getValue();
        if (fbObj == null) {
            return value;
        } else if (fbObj instanceof Long) {
            value += (long) fbObj;
        } else if (fbObj instanceof Double){
            value += (double) fbObj;
        } else {
            value += (String) fbObj;
        }
        return value;
    }

    public List<ScrimArea> getAllScrimAreas2() {
        // Get the ScimAreas via Firebase
        final List<ScrimArea> allAreas = new ArrayList<>();

        final Firebase vAreaRef = firebaseRef.child("VitalizeAreas");
        vAreaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Child: " + child.toString());
                    final ScrimArea area = child.getValue(ScrimArea.class);
                    VitalizeApplication.getAllAreas().add(area);
                }
                setChanged();
                notifyObservers();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return allAreas;
    }

    private class OnCompleteListener implements Firebase.CompletionListener {
        @Override
        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
            if (firebaseError == null) {
                Log.d(TAG, "Success!");
            } else {
                Log.d(TAG, "Error!");
            }
        }
    }
}

