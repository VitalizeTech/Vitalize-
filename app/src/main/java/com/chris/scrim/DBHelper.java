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

/**
 * Created by chris on 2/22/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
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
    private static final int DATABASE_VERSION = 1;
    private final Firebase firebaseRef;

    private static final String CREATE_DATABASE_STATEMENT = "create table " + TABLE_NAME + "(" + ID +" integer primary key autoincrement, " +
            TITLE + " text not null, " + ADDIT_INFO + " text not null, " + TYPE + " text not null, " + LOCATION_LAT
            + " double, " + LOCATION_LONG + " double, " +  NUM_SPOTS + " integer, " + DATE_IN_MILLIS + " integer)";
    public DBHelper(Context theContext) {
        super(theContext, DATABASE_NAME, null, DATABASE_VERSION);
        Firebase.setAndroidContext(theContext);
        firebaseRef = new Firebase(FIREBASE_LINK);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //dump the old version
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void insertScrimAreaDB(int id, String title, String additionalInfo, String type, double latitude, double longitude, int numSpots,
                                  Calendar date) {
        // Add vitalize area to sql db
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = getSharedContentUpdateInsertValues(title, additionalInfo, type, numSpots, date);
        contentValues.put(LOCATION_LAT, latitude);
        contentValues.put(LOCATION_LONG, longitude);
        contentValues.put(ID, id);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();
    }
    public int updateScrimAreaDB(int id, String title, String additionalInfo, String type, int numSpots, Calendar date) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int returnCode =  sqLiteDatabase.update(TABLE_NAME, getSharedContentUpdateInsertValues(title, additionalInfo, type, numSpots, date),
                ID + " = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
        return returnCode;
    }
    public void removeScrimAreaDB(int id) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();;
    }



    private ContentValues getSharedContentUpdateInsertValues(String title, String additionalInfo, String type, int numSpots, Calendar date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
        contentValues.put(ADDIT_INFO, additionalInfo);
        contentValues.put(TYPE, type);
        contentValues.put(NUM_SPOTS, numSpots);
        contentValues.put(DATE_IN_MILLIS, date.getTimeInMillis());
        return contentValues;
    }

    public List<ScrimArea> getAllScrimAreas() {
        List<ScrimArea> allAreas = new ArrayList<ScrimArea>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor getAll = db.rawQuery("select * from " + TABLE_NAME, null);
        if(getAll.moveToFirst()) {
            do {
                int id = getAll.getInt(getAll.getColumnIndex(ID));
                String title = getAll.getString(getAll.getColumnIndex(TITLE));
                String type = getAll.getString(getAll.getColumnIndex(TYPE));
                String additionalInfo = getAll.getString(getAll.getColumnIndex(ADDIT_INFO));
                double locationLatitude = getAll.getDouble(getAll.getColumnIndex(LOCATION_LAT));
                double locationLongitude = getAll.getDouble(getAll.getColumnIndex(LOCATION_LONG));
                int numSpots = getAll.getInt(getAll.getColumnIndex(NUM_SPOTS));
                long dateInMillis = getAll.getLong(getAll.getColumnIndex(DATE_IN_MILLIS));
                ScrimArea fromDatabase = new ScrimArea();
                Calendar calInMillis = Calendar.getInstance();
                calInMillis.setTimeInMillis(dateInMillis);
                fromDatabase.update(title, additionalInfo, VitalizeApplication.getTypeImage(type), VitalizeApplication.getMarkerImage(type),
                        numSpots, type, calInMillis.getTimeInMillis());
                fromDatabase.setId(id+ "");
                fromDatabase.setCenter(new LatLng(locationLatitude, locationLongitude));
                allAreas.add(fromDatabase);
            }while (getAll.moveToNext());
        }
        db.close();
        return allAreas;
    }



}

