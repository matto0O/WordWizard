package com.wordwiz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import static com.wordwiz.WordWizard.notificationHelper;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       notificationHelper = new NotificationHelper(context);
       NotificationCompat.Builder builder = notificationHelper.getNC();
       notificationHelper.getManager().notify(1,builder.build());
    }
}
