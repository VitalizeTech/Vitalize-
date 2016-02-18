package com.chris.scrim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    private EditText myEmail, myPassword, myPassConfirmation;
    private Button myRegButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myEmail = (EditText) findViewById(R.id.regEmail);
        myPassword = (EditText) findViewById(R.id.regPassword);
        myPassConfirmation = (EditText) findViewById(R.id.regPassConfirm);
        myRegButton = (Button) findViewById(R.id.regRegBtn);
    }
}
