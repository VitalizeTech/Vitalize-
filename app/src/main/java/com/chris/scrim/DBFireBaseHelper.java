package com.chris.scrim;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by chris on 2/22/2016.
 */
public class DBFireBaseHelper extends Observable {
    public static final String FIREBASE_LINK = "https://scrim.firebaseio.com/";
    private static final String TAG = DBFireBaseHelper.class.getName();
    private final Firebase firebaseRef;

    public DBFireBaseHelper(Context theContext) {
        Firebase.setAndroidContext(theContext);
        firebaseRef = new Firebase(FIREBASE_LINK);
        if (theContext instanceof MapsActivity) {
            addObserver((MapsActivity) theContext);
        }
    }

    public void insertScrimAreaInFirebase(ScrimArea newArea) {
        // Add to firebase
        // Push the new scrim area up to Firebase
        final Firebase vAreaRef = firebaseRef.child("VitalizeAreas").push();
        newArea.setId(vAreaRef.getKey());
        vAreaRef.setValue(newArea, new OnCompleteListener());
        // Add the eventId to the user
        final Firebase userAreaRef = firebaseRef.child("Users").child(firebaseRef.getAuth().getUid()).child("MyAreas");
        userAreaRef.push().setValue(vAreaRef.getKey(), new OnCompleteListener());
    }

    public void updateScrimAreaInFireBase(final ScrimArea area) {
        final Firebase vAreaRef = firebaseRef.child("VitalizeAreas").child(area.getId());
        vAreaRef.setValue(area, new OnCompleteListener());
    }

    public void removeScrimAreaInFirebase(final String vAreaFbId) {
        final Firebase vSingleAreaRef = firebaseRef.child("VitalizeAreas").child(vAreaFbId);
        vSingleAreaRef.removeValue(new OnCompleteListener());
        firebaseRef.child("Users")
                .child(firebaseRef.getAuth().getUid())
                .child("MyAreas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
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

    public List<ScrimArea> getAllScrimAreasFromFirebase() {
        // Get the ScimAreas via Firebase
        final List<ScrimArea> allAreas = new ArrayList<>();

        final Firebase vAreaRef = firebaseRef.child("VitalizeAreas");
        vAreaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Child: " + child.toString());
                    final ScrimArea area = child.getValue(ScrimArea.class);
                    for (DataSnapshot mems : child.child("members").getChildren()) {
                        String memId = (String) mems.getValue();
                        final User stub =  new User("jjones:|", "Stub", 128, R.drawable.krysten, R.drawable.moonlightbae);
                        stub.setId(memId);
                        area.getUsers().add(stub);
                    }
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

    public void joinEvent(String eventId) {
        Log.d("Vitalize", "WUT" + eventId);
        Firebase eventRef = firebaseRef.child("VitalizeAreas").child(eventId).child("members");
        eventRef.push().setValue(eventRef.getAuth().getUid());
    }

    public String getUserId() {
        return firebaseRef.getAuth().getUid();
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

//    public User getUserInfo(String id) {
//        firebaseRef
//    }
}

