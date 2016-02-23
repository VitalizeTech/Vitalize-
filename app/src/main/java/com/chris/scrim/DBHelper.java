package com.chris.scrim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 2/22/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Vitalize.db";
    private static final String TABLE_NAME = "events";
    private static final String TITLE = "title";
    private static final String ADDIT_INFO = "addit_info";
    private static final String TYPE = "type";
    private static final String LOCATION_LAT = "location_lat";
    private static final String LOCATION_LONG = "location_lon";
    private static final String NUM_SPOTS = "num_spots";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_DATABASE_STATEMENT = "create table " + TABLE_NAME + "(id integer primary key autoincrement, " +
            TITLE + " text not null, " + ADDIT_INFO + " text not null, " + TYPE + " text not null, " + LOCATION_LAT
            + " double, " + LOCATION_LONG + " double" +  NUM_SPOTS + " integer)";
    public DBHelper(Context theContext) {
        super(theContext, DATABASE_NAME, null, DATABASE_VERSION);
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

    public void insertData(String title, String additionalInfo, String type, double latitude, double longitude, int numSpots) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
        contentValues.put(ADDIT_INFO, additionalInfo);
        contentValues.put(TYPE, type);
        contentValues.put(LOCATION_LAT, latitude);
        contentValues.put(LOCATION_LONG, longitude);
        contentValues.put(NUM_SPOTS, numSpots);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }
    public List<ScrimArea> getAllScrimArea() {
        List<ScrimArea> allAreas = new ArrayList<ScrimArea>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor getAll = db.rawQuery("select * from " + TABLE_NAME, null);
        getAll.moveToFirst();
        while(!getAll.isAfterLast()) {
            String title = getAll.getString(getAll.getColumnIndex(TITLE));
            String type = getAll.getString(getAll.getColumnIndex(TYPE));
            String additionalInfo = getAll.getString(getAll.getColumnIndex(ADDIT_INFO));
            double locationLatitude = getAll.getDouble(getAll.getColumnIndex(LOCATION_LAT));
            double locationLongitude = getAll.getDouble(getAll.getColumnIndex(LOCATION_LONG));
            int numSpots = getAll.getInt(getAll.getColumnIndex(NUM_SPOTS));
            ScrimArea fromDatabase = new ScrimArea
        }
    }
}

