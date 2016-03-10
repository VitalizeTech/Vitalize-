package com.chris.scrim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class LoginActivity extends TouchActivity {
    public static String SHARED_PREFERENCES_FILE = "com.vitalize.PREFERENCE_FILE_KEY";
    public static String USERNAME_KEY = "username";

    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);
        setTouchNClick(findViewById(R.id.btnLogin));
        setTouchNClick(findViewById(R.id.btnReg));
    }

    @Override
    public void onStart() {
        super.onStart();
        ref = new Firebase("https://scrim.firebaseio.com/");

        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        Button loginBtn = (Button) findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = username.getText().toString();

                ref.authWithPassword(email, password.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE,
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String stubUsername = email.substring(0,email.indexOf("@"));
                        editor.putString(USERNAME_KEY, stubUsername);
                        editor.apply();
                        VitalizeApplication.loggedInId = authData.getUid();
                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(LoginActivity.this, "Oops something went wrong!", Toast.LENGTH_SHORT).show();
                        // there was an error //TODO: Check what error
                    }
                });
            }
        });

        Button regBtn = (Button) findViewById(R.id.btnReg);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}
