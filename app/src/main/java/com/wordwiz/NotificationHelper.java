package com.wordwiz;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    public static final String CHANNEL = "channel";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        createNotificationChannels();
    }

    public void createNotificationChannels(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            String a="";
            a+=R.string.dailyLearningNotifications;
            NotificationChannel nc = new NotificationChannel(
                    CHANNEL, a,
                    NotificationManager.IMPORTANCE_HIGH);
            getManager().createNotificationChannel(nc);

            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(nc);
        }
    }

    public NotificationManager getManager(){
        if(manager==null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNC (){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL)
                .setContentText("Hurry up! Your streak will end soon.")
                .setContentTitle("Test yourself!")
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.ic_alarm);
    }

    public NotificationCompat.Builder getNC2 (){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL)
                .setContentText("We miss you :(")
                .setContentTitle("Test yourself!")
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.ic_notif);
    }

}
