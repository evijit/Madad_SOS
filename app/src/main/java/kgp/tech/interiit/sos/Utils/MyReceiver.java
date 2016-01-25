package kgp.tech.interiit.sos.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import kgp.tech.interiit.sos.Utils.NetworkLocationService;
import kgp.tech.interiit.sos.WelcomeActivity;

/**
 * Created by nishantiam on 20-01-2016.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent!=null)
            if(intent.getAction()!=null)// Here to start the ap anytime
            {
                Log.e("Intent :", intent.getAction());
                Intent activity1 = new Intent(context, WelcomeActivity.class);
                activity1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activity1);
            }
        Intent service1 = new Intent(context, NetworkLocationService.class);
        context.startService(service1);
        Log.e("MyReceiver", "received");


    }
}
