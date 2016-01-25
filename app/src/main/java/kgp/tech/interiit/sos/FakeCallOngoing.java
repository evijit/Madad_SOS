package kgp.tech.interiit.sos;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

public class FakeCallOngoing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call_ongoing);

        final TextView tv=(TextView)findViewById(R.id.timer);

        int secondsToRun = 999;

        ValueAnimator timer = ValueAnimator.ofInt(secondsToRun);
        timer.setDuration(secondsToRun * 1000).setInterpolator(new LinearInterpolator());
        timer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int elapsedSeconds = (int) animation.getAnimatedValue();
                int minutes = elapsedSeconds / 60;
                int seconds = elapsedSeconds % 60;

                tv.setText(String.format("%d:%02d", minutes, seconds));
            }
        });
        timer.start();
    }

    public void closething(View v)
    {
        finish();
    }
}
