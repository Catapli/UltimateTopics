package com.example.topics.Adaptadores;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.example.topics.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class PushNotificationsService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        HashMap<String,Object> tokenData = new HashMap<>();
        tokenData.put("token",s);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        firestore.collection("users").document(email).set(tokenData, SetOptions.merge());
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String tittle = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        final String CHANNEL_ID = "HEADS_UP_NOTIFICATIONS";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "MyNotifications",
                NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(tittle)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_logo_topics)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1,notification.build());
    }
}
