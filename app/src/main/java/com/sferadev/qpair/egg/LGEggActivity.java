package com.sferadev.qpair.egg;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.lge.app.floating.FloatableActivity;
import com.sferadev.qpair.R;

public class LGEggActivity extends FloatableActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.egg);
        Egg world = (Egg) findViewById(R.id.world);
        world.setScoreField((TextView) findViewById(R.id.score));
        world.setSplash(findViewById(R.id.welcome));
        Log.v("HubToDate", "focus: " + world.requestFocus());
    }
}