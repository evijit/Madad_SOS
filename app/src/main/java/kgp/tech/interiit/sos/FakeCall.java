package kgp.tech.interiit.sos;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fima.glowpadview.GlowPadView;

public class FakeCall extends AppCompatActivity implements GlowPadView.OnTriggerListener {

    MediaPlayer player;
    private GlowPadView mGlowPadView;
    CountDownTimer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        player = MediaPlayer.create(this,
                Settings.System.DEFAULT_RINGTONE_URI);
        player.setVolume(1,1);
        player.start();

        mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);

        mGlowPadView.setOnTriggerListener(this);

        // uncomment this to make sure the glowpad doesn't vibrate on touch
        // mGlowPadView.setVibrateEnabled(false);

        // uncomment this to hide targets
        //mGlowPadView.setShowTargetsOnIdle(true);

        t = new CountDownTimer( Long.MAX_VALUE , 100) {

            // This is called every interval. (Every 10 seconds in this example)
            public void onTick(long millisUntilFinished) {
               // Log.d("test","Timer tick");
                mGlowPadView.ping();


            }

            public void onFinish() {
               // Log.d("test","Timer last tick");
                start();
            }
        }.start();
    }

    @Override
    protected void onStop() {

        player.stop();
        t.cancel();
        super.onStop();
    }

    @Override
    public void onGrabbed(View v, int handle) {

    }

    @Override
    public void onReleased(View v, int handle) {
        mGlowPadView.ping();

    }

    @Override
    public void onTrigger(View v, int target) {
        final int resId = mGlowPadView.getResourceIdForTarget(target);
        switch (resId) {
            case R.drawable.ic_item_accept:
                Toast.makeText(this, "Call accepted!!!", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(FakeCall.this, FakeCallOngoing.class);
                startActivity(i);
                player.stop();
                t.cancel();
                finish();
                break;

            case R.drawable.ic_item_decline:
                Toast.makeText(this, "Call Declined!!!", Toast.LENGTH_SHORT).show();
                player.stop();
                t.cancel();
                finish();

                break;
            default:
                // Code should never reach here.
        }
    }

    @Override
    public void onGrabbedStateChange(View v, int handle) {

    }

    @Override
    public void onFinishFinalAnimation() {

    }
}
