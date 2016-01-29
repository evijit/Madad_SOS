package kgp.tech.interiit.sos;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import kgp.tech.interiit.sos.Utils.SirenService;
import kgp.tech.interiit.sos.Utils.Utils;
import kgp.tech.interiit.sos.Utils.comm;

public class AnimatedButtons extends AppCompatActivity {
    public static boolean isSiren = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enterFromBottomAnimation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animated_buttons);

        ImageButton i1= (ImageButton) findViewById(R.id.fab1);
        ImageButton i2= (ImageButton) findViewById(R.id.fab2);
        ImageButton i3= (ImageButton) findViewById(R.id.fab3);
        ImageButton i4= (ImageButton) findViewById(R.id.fab4);
        ImageButton i5= (ImageButton) findViewById(R.id.fab5);
        ImageButton i6= (ImageButton) findViewById(R.id.fab6);
        ImageButton i7= (ImageButton) findViewById(R.id.fab7);
        FloatingActionButton ix= (FloatingActionButton) findViewById(R.id.fab);




        View[] views = new View[] {i7,i1,i2,i3,i4,i5,i6,ix };

// 100ms delay between Animations
        long delayBetweenAnimations = 100l;

        for(int i = 0; i < views.length; i++) {
            final View view = views[i];

            // We calculate the delay for this Animation, each animation starts 100ms
            // after the previous one
            long delay = i * delayBetweenAnimations;

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.expand_in);
                    view.startAnimation(animation);
                }
            }, delay);
        }


    }

    public void onbkps(View v)
    {
        finish();
    }

    protected void enterFromBottomAnimation(){
        overridePendingTransition(R.anim.activity_open_translate_from_bottom, R.anim.activity_no_animation);
    }

    protected void exitToBottomAnimation(){
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_close_translate_to_bottom);
    }

    public void action_sos(View v)
    {
        Intent intent = new Intent(this, CreateSOSActivity.class);
        startActivity(intent);
        finish();
    }

    public void action_cancel_sos(View v)
    {
        Utils.showDialog(this, R.string.cancel_sos, R.string.yes, R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE:
                        // int which = -2

                        break;
                    case DialogInterface.BUTTON_POSITIVE:
                        SharedPreferences sp = getSharedPreferences("SOS", Context.MODE_PRIVATE);
                        String SOSid = sp.getString("sosID", null);

                        ParseQuery<ParseObject> pq = ParseQuery.getQuery("SOS");
                        pq.getInBackground(SOSid, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e != null) {
                                    Utils.showDialog(AnimatedButtons.this, e.getMessage());
                                    return;
                                }
                                parseObject.put("isActive", false);
                                final ProgressDialog dia = ProgressDialog.show(AnimatedButtons.this, null, getString(R.string.alert_wait));
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e!=null) {
                                            Utils.showDialog(AnimatedButtons.this, e.getMessage());
                                            return;
                                        }

                                        SharedPreferences sp = getSharedPreferences("SOS", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("sosID", null);
                                        dia.dismiss();

                                        Intent intent = new Intent(AnimatedButtons.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        });
                        //TODO call the cloud service and make it check if contact uses the app
                        break;
                }
                return;
            }
        });
    }

    public void recordAudio(View v) {
        Intent intent = new Intent(this, CreateSOSActivity.class);
        startActivity(intent);
        finish();
    }

    public void action_fakecall(View v)
    {
        Intent intent = new Intent(this, FakeCallReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1222222, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        if (mTimer.getText().toString().trim().equalsIgnoreCase("5 sec")) {
            int i = 5;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
//        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//        {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
//        }
//        else
//        {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
        //}

        Toast.makeText(this, "Fake call scheduled after " + i + " sec", Toast.LENGTH_LONG).show();
//        }

//        else if (mTimer.getText().toString().trim().equalsIgnoreCase("10 sec")) {
//            int i = 10;
//            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (i * 1000), pendingIntent);
//            Toast.makeText(this, "Fake call scheduled after " + i + " sec",Toast.LENGTH_LONG).show();
//        }
        finish();
    }

    public void enable_hospitals(View v)
    {
        MyMapFragment map_fragment = new MyMapFragment();
        map_fragment.isAddHospital = !(map_fragment.isAddHospital);
        if(map_fragment.isAddHospital)
            Toast.makeText(this, "Nearby Hospitals Marked", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Nearby Hospitals Un-Marked", Toast.LENGTH_LONG).show();
        map_fragment = null;
        System.gc();
        finish();
    }

    public void enable_police(View v)
    {
        MyMapFragment map_fragment = new MyMapFragment();
        map_fragment.isAddPolice= !(map_fragment.isAddPolice);
        if(map_fragment.isAddPolice)
            Toast.makeText(this, "Nearby Police Stations Marked", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Nearby Police Staions Un-Marked", Toast.LENGTH_LONG).show();
        map_fragment = null;
        System.gc();
        finish();
    }

    public void enable_pharmacy(View v)
    {
        MyMapFragment map_fragment = new MyMapFragment();
        map_fragment.isAddPharmacy = !(map_fragment.isAddPharmacy);
        if(map_fragment.isAddPharmacy)
            Toast.makeText(this, "Nearby Pharmacies Marked", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Nearby Pharmacies Un-Marked", Toast.LENGTH_LONG).show();
        map_fragment = null;
        System.gc();
        finish();
    }
    public void action_siren (View v){
        Log.e("NextActivity", "startNotification");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("SOS APP")
                        .setContentText("Touch to Stop the Siren!");
        Intent resultIntent = new Intent(this, SirenService.class);
        resultIntent.putExtra("isSiren",false);
        PendingIntent pIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), resultIntent, 0);

        mBuilder.setContentIntent(pIntent);
        mBuilder.setOngoing(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1234, mBuilder.build());
        Intent serv = new Intent(this,SirenService.class);
        Intent serv2 = new Intent(this,SirenService.class);
        if(isSiren) {
            serv.putExtra("isSiren", false);
            serv2.putExtra("isSiren",true);
            isSiren = false;
        }
        else{
            serv.putExtra("isSiren",true);
            serv2.putExtra("isSiren", false);
            isSiren = true;
        }
        stopService(serv2);
        startService(serv);
        finish();

    }
    @Override
    protected void onPause() {
        exitToBottomAnimation();
        super.onPause();
    }
}
