package com.chris.scrim;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.Firebase;

import java.security.acl.Group;

public class ChatActivity extends TouchActivity {

    private static final String FIREBASE_URL = "https://scrim.firebaseio.com/";

    private Firebase mFireBase;
    private EditText messageToSend;
    private MessageListAdapter messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mFireBase = new Firebase(FIREBASE_URL).child("chat");
        messageToSend = (EditText)findViewById(R.id.message_to_send);

        messageAdapter = new MessageListAdapter(this, mFireBase.limitToFirst(50));
        Button sendButton = (Button)findViewById(R.id.btnSend);
        setTouchNClick(sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.this.sendMessage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ListView messagesList  = (ListView)findViewById(R.id.message_list);
        messagesList.setAdapter(messageAdapter);
    }

    private void sendMessage() {
        String textToSend = messageToSend.getText().toString();
        if (!textToSend.equals("")) {
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
            String userName = sharedPreferences.getString(LoginActivity.USERNAME_KEY, "");
           //  Create our 'model', a Chat object
            Chat message = new Chat(textToSend, userName);
           //  Create a new, auto-generated child of that chat location, and save our chat data there
            mFireBase.push().setValue(message);
            messageToSend.setText("");
        }
    }
}
