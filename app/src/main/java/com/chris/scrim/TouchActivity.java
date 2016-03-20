package com.chris.scrim;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chris on 3/2/2016.
 */
public class TouchActivity extends AppCompatActivity {
    public static final TouchEffect TOUCH = new TouchEffect();

    public void setTouchNClick(View v) {
        if (v != null)
            v.setOnTouchListener(TOUCH);
    }

    static class TouchEffect implements View.OnTouchListener
    {

        /* (non-Javadoc)
         * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                Drawable d = v.getBackground();
                d.mutate();
                d.setAlpha(150);
                v.setBackgroundDrawable(d);
            }
            else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL)
            {
                Drawable d = v.getBackground();
                d.setAlpha(255);
                v.setBackgroundDrawable(d);
            }
            return false;
        }

    }

}
