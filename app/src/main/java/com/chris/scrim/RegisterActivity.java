package com.chris.scrim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class RegisterActivity extends TouchActivity {
    private EditText myEmail, myPassword, myPassConfirmation;
    private Button myRegButton;
    private Firebase ref;
    private DBFireBaseHelper dbHelperRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Firebase.setAndroidContext(this);
        dbHelperRef = new DBFireBaseHelper(this);
        ref = new Firebase(DBFireBaseHelper.FIREBASE_LINK);
        myEmail = (EditText) findViewById(R.id.email);
        myPassword = (EditText) findViewById(R.id.pwd);
        myPassConfirmation = (EditText) findViewById(R.id.confirmPwd);
        myRegButton = (Button) findViewById(R.id.btnReg);

        setTouchNClick(myRegButton);
        myRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if(/*!myPassword.getText().toString().equals("") // Pass not blank
                        && !myEmail.getText().toString().equals("") // Email not blank
                        && */myPassword.getText().toString().equals(myPassConfirmation.getText().toString())) { // Pass is the same
                    ref.createUser(myEmail.getText().toString(), myPassword.getText().toString(), new RegResultHandler());

                } else {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                }
                v.setEnabled(true);
            }
        });
    }

    private class RegResultHandler implements Firebase.ResultHandler {
        @Override
        public void onSuccess() {
            Toast.makeText(RegisterActivity.this, "User Created!", Toast.LENGTH_SHORT).show();
            ref.authWithPassword(myEmail.getText().toString(), myPassword.getText().toString(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    String username = ((EditText)findViewById(R.id.username)).getText().toString();
                    dbHelperRef.storeUsername(authData.getUid(), username);
                    Toast.makeText(RegisterActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                    VitalizeApplication.intializeLocalUser(authData.getUid(), username);

                    Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Toast.makeText(RegisterActivity.this, "Oops something went wrong!", Toast.LENGTH_SHORT).show();
                    // there was an error //TODO: Check what error
                }
            });

            Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        @Override
        public void onError(FirebaseError firebaseError) {
            Toast.makeText(RegisterActivity.this, "Oops something went wrong!", Toast.LENGTH_SHORT).show();
            // there was an error //TODO: Check what error
        }
    }
}

