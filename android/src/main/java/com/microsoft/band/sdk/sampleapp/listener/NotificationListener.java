package com.microsoft.band.sdk.sampleapp.listener;

import android.app.Notification;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.microsoft.band.sdk.sampleapp.tools.translation.Client;

public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            super.onNotificationPosted(sbn);

            Notification notification = null;

            getActiveNotifications();

            if (sbn != null) {
                notification = sbn.getNotification();
            }

            if (notification != null && notification.tickerText != null) {

                String strMsg = notification.tickerText.toString();

                Client.getInstance(this.getApplicationContext()).translate(strMsg);
            }
        } catch(Exception e) {
            Log.e("ERROR", "exception", e);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
