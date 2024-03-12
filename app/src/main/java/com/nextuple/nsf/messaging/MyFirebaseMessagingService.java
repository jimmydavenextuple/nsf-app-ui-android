package com.nextuple.nsf.messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nextuple.nsf.MainActivity;
import com.nextuple.nsf.R;
import com.nextuple.nsf.service.LogService;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final LogService logService;

    public MyFirebaseMessagingService(LogService logService) {
        this.logService = logService;
    }

    @Override
    public void onNewToken(@NonNull String token) {
        logService.trackEvent("ReceivedNewToken", Map.of("NewToken", token));


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token, logService);
    }


    public static void sendRegistrationToServer(String token, LogService logService) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/fcm_tokens");
        String user = "User_NT3"; // Replace with the user's identifier

        // Write the FCM registration token to the database under the user's ID.
        databaseReference.child(user).setValue(token)
                .addOnSuccessListener(unused -> logService.trackEvent(
                        "TokenSavedInFirebaseDb",
                        Map.of("token", token)))
                .addOnFailureListener(e -> logService.trackError(
                        "TokenNotSaveInFirebaseDb",
                        e,
                        Map.of("token", token)));

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            logService.trackEvent("MessageReceived",
                    Map.of("MessageId", remoteMessage.getMessageId()==null?"":remoteMessage.getMessageId(),
                            "Title", notification.getTitle()==null?"":notification.getTitle(),
                            "From", remoteMessage.getFrom()==null?"":remoteMessage.getFrom(),
                            "Body", notification.getBody()==null?"":notification.getBody()
                    )
            );
            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
            sendNotification(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification());
        }
    }

    private void sendNotification(String from, String body) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(
                        MyFirebaseMessagingService.this.getApplicationContext(),
                        from + " -> " + body, Toast.LENGTH_SHORT)
                        .show()
        );
    }

    private void sendNotification(RemoteMessage.Notification notification) {
        // Create a PendingIntent that will start the PickScreen composable when the notification is tapped.
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("notificationRoute", Route.PICK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        String channelId = "NotificationChannel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification androidNotification =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_circle_notification)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT));

        notificationManager.notify(0, androidNotification);
    }
}