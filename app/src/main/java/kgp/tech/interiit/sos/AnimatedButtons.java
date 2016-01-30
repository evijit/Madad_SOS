package kgp.tech.interiit.sos;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

import kgp.tech.interiit.sos.Utils.SirenService;
import kgp.tech.interiit.sos.Utils.Utils;
import kgp.tech.interiit.sos.Utils.comm;

public class AnimatedButtons extends AppCompatActivity {
    public static boolean isSiren = false;

    ImageButton mRecordButton= null;
    private static String mFileName = null;
    private static final String LOG_TAG = "AudioShareable";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enterFromBottomAnimation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animated_buttons);

        ImageButton i1= (ImageButton) findViewById(R.id.fab1);
        ImageButton mRecordButton = (ImageButton) findViewById(R.id.fab1);
        ImageButton i2= (ImageButton) findViewById(R.id.fab2);
        ImageButton i3= (ImageButton) findViewById(R.id.fab3);
        ImageButton i4= (ImageButton) findViewById(R.id.fab4);
        ImageButton i5= (ImageButton) findViewById(R.id.fab5);
        ImageButton i6= (ImageButton) findViewById(R.id.fab6);
        ImageButton i7= (ImageButton) findViewById(R.id.fab7);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audioShareable.mp3";

        mRecordButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    //((Button) v).setText("Stop recording");
                } else {
                    //((Button) v).setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        });

        i5.setBackgroundResource(R.drawable.fab_purple);
        i6.setBackgroundResource(R.drawable.fab_grey);
        i1.setBackgroundResource(R.drawable.fab_blue);
        i2.setBackgroundResource(R.drawable.fab_ochre);
        i7.setBackgroundResource(R.drawable.fab_red);
        i3.setBackgroundResource(R.drawable.fab_green);
        i4.setBackgroundResource(R.drawable.fab_amber);


        SharedPreferences sp = getSharedPreferences("SOS", Context.MODE_APPEND | Context.MODE_PRIVATE);

        if(sp.getString("sosID", null)!=null)
        {
            i7.setImageResource(R.drawable.ic_done_white_24dp);
            i7.setBackgroundResource(R.drawable.fab_green);
            findViewById(R.id.holder).setBackgroundColor(ContextCompat.getColor(this, R.color.darkred));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.darkred));
            }


        }
        FloatingActionButton ix= (FloatingActionButton) findViewById(R.id.fab);




        View[] views = new View[] {i7,i1,i2,i3,i4,i5,i6,ix };

// 100ms delay between Animations
        long delayBetweenAnimations = 80l;

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
        SharedPreferences sp = getSharedPreferences("SOS", Context.MODE_APPEND | Context.MODE_PRIVATE);

        if(sp.getString("sosID", null)!=null)
        {
            action_cancel_sos(v);
            return;
        }
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
                                        editor.commit();
                                        dia.dismiss();

                                        Intent intent = new Intent(AnimatedButtons.this, WelcomeActivity.class);
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


//    private ImageButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }
    //mRecordButton.setText("Start recording");


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        shareAudio();
    }

    private void shareAudio() {
        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("audio/*");

        // Make sure you put example png image named myImage.png in your
        // directory
        String imagePath = Environment.getExternalStorageDirectory()
                + "/audioShareable.mp3";

        File imageFileToShare = new File(imagePath);

        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(share, "Share Audio!"));
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
            Toast.makeText(this, "Showing nearby Hospitals", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Removing Hospitals", Toast.LENGTH_LONG).show();
        map_fragment = null;
        System.gc();
        finish();
    }

    public void enable_police(View v)
    {
        MyMapFragment map_fragment = new MyMapFragment();
        map_fragment.isAddPolice= !(map_fragment.isAddPolice);
        if(map_fragment.isAddPolice)
            Toast.makeText(this, "Showing nearby Police Stations", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Removing Police Stations", Toast.LENGTH_LONG).show();
        map_fragment = null;
        System.gc();
        finish();
    }

    public void enable_pharmacy(View v)
    {
        MyMapFragment map_fragment = new MyMapFragment();
        map_fragment.isAddPharmacy = !(map_fragment.isAddPharmacy);
        if(map_fragment.isAddPharmacy)
            Toast.makeText(this, "Showing nearby Pharmacies", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Removing Pharmacies", Toast.LENGTH_LONG).show();
        map_fragment = null;
        System.gc();
        finish();
    }
    public void action_siren (View v){
        Log.e("NextActivity", "startNotification");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("Madad")
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
