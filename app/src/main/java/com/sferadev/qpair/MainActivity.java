package com.sferadev.qpair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sferadev.qpair.service.ShakeService;
import com.shamanland.fab.FloatingActionButton;

import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;
import static com.sferadev.qpair.utils.Utils.openURL;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog("Your opinion matters!", "Enter HubToDate's community and share your input!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openURL("https://plus.google.com/communities/102943838378590125127");
                    }
                }, null);

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
