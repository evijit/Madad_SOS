package kgp.tech.interiit.sos.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import kgp.tech.interiit.sos.CreateSOSActivity;
import kgp.tech.interiit.sos.Utils.NetworkLocationService;
import kgp.tech.interiit.sos.WelcomeActivity;

/**
 * Created by nishantiam on 20-01-2016.
 */
public class MyReceiver extends BroadcastReceiver {
    private static long pre_time = 0;
    private static int how_much_consec = 0;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent!=null)
            if(intent.getAction()!=null )// Here to start the ap anytime
            {
                if(intent.getAction().compareTo("android.intent.action.SCREEN_ON")==0 || intent.getAction().compareTo("android.intent.action.SCREEN_OFF")==0 ) {
                    if (isConsecutive() == true)
                    {
                        Log.e("MyReceiver", "Call SOS");
                        Intent i= new Intent(context, CreateSOSActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }

                    //Intent activity1 = new Intent(context, WelcomeActivity.class);
                    //activity1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //context.startActivity(activity1);
                }
            }
        Intent service1 = new Intent(context, NetworkLocationService.class);
        context.startService(service1);
        Log.e("MyReceiver", "received");


    }

    public boolean isConsecutive()
    {
        Log.e("Consecutive: ",Integer.toString(how_much_consec));
        long curr_time = System.currentTimeMillis();

        if(how_much_consec == 4 )
        {
            how_much_consec = 0;
            pre_time = curr_time;
            return true;
        }
        else
        {
            if(curr_time - pre_time < 1500 ) {
                how_much_consec++;
            }
            else{
                how_much_consec = 0;
            }
            pre_time = curr_time;
            return false;

        }


    }

}
