package com.example.whatsapclone;

import android.content.Intent;
import android.media.session.MediaSession;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MymessageService extends FirebaseMessagingService {
    public static final String Toeken_broadcase="fcmtokenbroadcast";
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Toast.makeText(this, "new Token"+token, Toast.LENGTH_SHORT).show();

        getApplicationContext().sendBroadcast(new Intent(token));
        storeToken(token);
    }

    private void storeToken(String token) {

    }

}
