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
import java.util.Map;
import java.util.Observable;

/**
 * Created by chris on 2/22/2016.
 */
public class DBHelper extends Observable {
    public static final String FIREBASE_LINK = "https://scrim.firebaseio.com/";
    private static final String TAG = DBHelper.class.getName();
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

    public void updateScrimAreaDB2(final ScrimArea area) {
        final Firebase vAreaRef = firebaseRef.child("VitalizeAreas").child(area.getId());
        vAreaRef.setValue(area, new OnCompleteListener());
    }

    public void removeScrimAreaDB2(final String vAreaFbId) {
        final Firebase vSingleAreaRef = firebaseRef.child("VitalizeAreas").child(vAreaFbId);
        vSingleAreaRef.removeValue(new OnCompleteListener());
        firebaseRef.child("Users")
                .child(firebaseRef.getAuth().getUid())
                .child("MyAreas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    final String theId = (String) child.getValue();
                    if (theId.equals(vAreaFbId)) {
                        child.getRef().removeValue(new OnCompleteListener());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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

