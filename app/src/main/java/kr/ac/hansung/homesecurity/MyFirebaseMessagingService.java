package kr.ac.hansung.homesecurity;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

/**
 * Created by sky on 2017-11-07.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String msgBody = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + msgBody);
        }
    }
}