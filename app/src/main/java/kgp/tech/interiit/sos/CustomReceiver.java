package kgp.tech.interiit.sos;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by sayan on 22/1/16.
 */
public class CustomReceiver extends ParsePushBroadcastReceiver  {

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        //super.onPushOpen(context, intent);
        Intent i = new Intent(context, AcceptSOS.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
