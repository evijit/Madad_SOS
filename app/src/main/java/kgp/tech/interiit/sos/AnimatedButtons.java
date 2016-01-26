package kgp.tech.interiit.sos;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import kgp.tech.interiit.sos.Utils.comm;

public class AnimatedButtons extends AppCompatActivity {

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
        comm.sendSOS();
    }
    public void action_fakecall(View v)
    {
        Intent intent = new Intent(this, FakeCallReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1222222, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        if (mTimer.getText().toString().trim().equalsIgnoreCase("5 sec")) {
            int i = 5;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
        }
        else
        {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (i * 1000), pendingIntent);
        }

        Toast.makeText(this, "Fake call scheduled after " + i + " sec", Toast.LENGTH_LONG).show();
//        }

//        else if (mTimer.getText().toString().trim().equalsIgnoreCase("10 sec")) {
//            int i = 10;
//            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (i * 1000), pendingIntent);
//            Toast.makeText(this, "Fake call scheduled after " + i + " sec",Toast.LENGTH_LONG).show();
//        }
        finish();
    }

    @Override
    protected void onPause() {
        exitToBottomAnimation();
        super.onPause();
    }
}
