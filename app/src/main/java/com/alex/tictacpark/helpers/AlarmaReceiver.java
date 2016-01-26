package com.alex.tictacpark.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;

/**
 * Created by Alex on 25/01/2016.
 */
public class AlarmaReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.e("Alarma: ", "Activación");

        // Tono
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        ringtone.play();

        // Notificación
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("alarma",false);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // Se cancela la notificación al clickarla
        builder.setAutoCancel(true);

        Notification notification = builder.setContentTitle("Retire el coche del parking")
                .setContentText("Pulse aquí para desactivar la alarma")
                .setTicker("Alarma TicTacPark")
                .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
