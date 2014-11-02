package com.sferadev.qpair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sferadev.qpair.service.ShakeService;
import com.sferadev.qpair.utils.Utils;
import com.shamanland.fab.FloatingActionButton;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FAB Clicked TODO
            }
        });

        if (!Utils.isServiceRunning(ShakeService.class)) {
            Intent i = new Intent(this, ShakeService.class);
            this.startService(i);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

}
