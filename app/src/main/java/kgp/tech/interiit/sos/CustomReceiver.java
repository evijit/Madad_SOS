package kgp.tech.interiit.sos;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sayan on 22/1/16.
 */
public class CustomReceiver extends ParsePushBroadcastReceiver {

//    @Override
//    protected void onPushOpen(Context context, Intent intent) {
//        //super.onPushOpen(context, intent);
//        Intent i = new Intent(context, AcceptSOS.class);
//        i.putExtras(intent.getExtras());
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
//    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, AcceptSOS.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtras(intent);

        PendingIntent piIntent=PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        String SOSId = null;
        String senderId = null;
        String channelId = null;
        try {
            JSONObject data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            SOSId = data.getString("sosId");
            senderId = data.getString("username");
            channelId = data.getString("chatChannel");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setContentTitle("Madad!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(senderId + " needs your help!\nYour help can save someones life."))
                .setContentText(senderId + " needs your help!").setAutoCancel(true)
                .setColor(Color.GREEN)
                .setFullScreenIntent(piIntent, true)
                .setPriority(2)
                .setLights(Color.RED, 500, 100)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true);
        mBuilder.setContentIntent(piIntent);
        return mBuilder.build();
    }
}
