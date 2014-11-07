package com.sferadev.qpair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sferadev.qpair.service.ShakeService;
import com.shamanland.fab.FloatingActionButton;

import static com.sferadev.qpair.utils.QPairUtils.EXTRA_PEER_VERSION;
import static com.sferadev.qpair.utils.QPairUtils.getQPairProperty;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createToast("QPair version is: " + getQPairProperty(EXTRA_PEER_VERSION));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                createToast(MainActivity.this, "Hi", "This is a test").show(getFragmentManager());
            }
        });

        // Load Shake Service if off
        if (!isServiceRunning(ShakeService.class)) {
            Intent i = new Intent(this, ShakeService.class);
            this.startService(i);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

}
