package kgp.tech.interiit.sos;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AnimatedButtons extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enterFromBottomAnimation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animated_buttons);

        FloatingActionButton f=(FloatingActionButton)findViewById(R.id.fab1);
        f.animate();
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


    public void fakecall(View v)
    {
        Intent intent = new Intent(this, FakeCallReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 1222222, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        if (mTimer.getText().toString().trim().equalsIgnoreCase("5 sec")) {
            int i = 10;
            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (i * 1000), pendingIntent);
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
