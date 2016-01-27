package kgp.tech.interiit.sos.Utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import kgp.tech.interiit.sos.HomeActivity;
import kgp.tech.interiit.sos.R;

/**
 * Created by nishantiam on 27-01-2016.
 */
public class SirenService extends Service {
    MediaPlayer mp;


    public SirenService(){
        super();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("SOSService", "onDestroy");
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        mp = new MediaPlayer();
        Log.e("Siren Service", "Created");

    }

    public void onCreate(Intent i)
    {
        mp = new MediaPlayer();
        Log.e("Siren Service", "Created with Intent");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("SirenService", "onStartCommand");
        mp = MediaPlayer.create(this, R.raw.siren);
        mp.setVolume(8f, 8f);
        mp.setLooping(true);
        Boolean shouldPlay = intent.getBooleanExtra("isSiren", false);
        Log.e("onStart",Boolean.toString(shouldPlay));
        if(shouldPlay) {
            final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            int origionalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            mp.start();
        }
        else {
            if(mp!=null && mp.isPlaying()){
                mp.stop();
            }
            mp.release();
            mp=null;
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1234);
        }
// Creates an explicit intent for an Activity in your app


// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.



        return START_NOT_STICKY;
    }
}
