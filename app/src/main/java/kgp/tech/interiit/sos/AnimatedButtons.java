package kgp.tech.interiit.sos;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

    @Override
    protected void onPause() {
        exitToBottomAnimation();
        super.onPause();
    }
}
