package com.chris.scrim;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.Firebase;

/**
 * Created by Aaron on 2/23/2016.
 */

public class SplashAuthCheck extends AppCompatActivity {
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://vitalize.firebaseio.com/");
        Intent nextIntent;
        if (ref.getAuth() == null) {
            nextIntent = new Intent(this, LoginActivity.class);
        } else {
            nextIntent = new Intent(this, MapsActivity.class);
        }
        nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(nextIntent);
        finish();
    }
}
