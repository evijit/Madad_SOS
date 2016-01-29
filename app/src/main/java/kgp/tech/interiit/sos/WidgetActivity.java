package kgp.tech.interiit.sos;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseObject;

import kgp.tech.interiit.sos.Utils.comm;

/**
 * Created by sayan on 27/1/16.
 */
public class WidgetActivity extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent sos_intent = getIntent();
        if(sos_intent == null)
            return;
        else if(sos_intent.getExtras().getString("type").equals("sos")){
            Log.d("Widget", "SOS");
            Toast.makeText(this, "SOS", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CreateSOSActivity.class);
            startActivity(intent);
            finish();
        }else if(sos_intent.getExtras().getString("type").equals("call")){
            Log.d("Widget", "Fake");
            Intent intent = new Intent(this, FakeCallReciever.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1222222, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            int i = 5;
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
            Toast.makeText(this, "Fake call scheduled after " + i + " sec", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "CALL", Toast.LENGTH_SHORT).show();
            finish();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }
}
