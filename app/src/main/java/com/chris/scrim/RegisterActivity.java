package com.chris.scrim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class RegisterActivity extends AppCompatActivity {
    private EditText myEmail, myPassword, myPassConfirmation;
    private Button myRegButton;
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Firebase.setAndroidContext(this);
        ref = new Firebase("https://scrim.firebaseio.com/");
        myEmail = (EditText) findViewById(R.id.regEmail);
        myPassword = (EditText) findViewById(R.id.regPassword);
        myPassConfirmation = (EditText) findViewById(R.id.regPassConfirm);
        myRegButton = (Button) findViewById(R.id.regRegBtn);

        myRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(/*!myPassword.getText().toString().equals("") // Pass not blank
                        && !myEmail.getText().toString().equals("") // Email not blank
                        && */myPassword.getText().toString().equals(myPassConfirmation.getText().toString())) { // Pass is the same
                    Toast.makeText(RegisterActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    ref.createUser(myEmail.getText().toString(), myPassword.getText().toString(), new Firebase.ResultHandler() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(RegisterActivity.this, "User Created!", Toast.LENGTH_SHORT).show();

                            ref.authWithPassword(myEmail.getText().toString(), myPassword.getText().toString(), new Firebase.AuthResultHandler() {
                                @Override
                                public void onAuthenticated(AuthData authData) {
                                    Toast.makeText(RegisterActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
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
                    });
                }
            }
        });
    }
}



//                    @Override
//                    public void onAuthenticated(AuthData authData) {
////                        Toast.makeText(RegisterActivity.this, "User Created!", Toast.LENGTH_SHORT).show();
////                        Intent intent = new Intent(RegisterActivity.this, MapsActivity.class);
////                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onAuthenticationError(FirebaseError firebaseError) {
////                        Toast.makeText(RegisterActivity.this, "You done fucked up!", Toast.LENGTH_SHORT).show();
//                        // there was an error //TODO: Check what error
//                    }
