package com.rochatech.oridriver;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Raul M Rocha on 7/27/2018.
 * Rocha Technologies de Mexico SA de CV
 * soporte@rochatech.com
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseService";

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG,"getId: " + FirebaseInstanceId.getInstance().getId());
        Log.d(TAG,"getInstanceId: " + FirebaseInstanceId.getInstance().getInstanceId());
//        Check if message contains a data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
//        Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
    }

}
