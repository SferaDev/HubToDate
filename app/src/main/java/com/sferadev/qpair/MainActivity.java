package com.sferadev.qpair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sferadev.qpair.service.ListenerService;

import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;
import static com.sferadev.qpair.utils.Utils.openURL;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fabItem1 = (FloatingActionButton) findViewById(R.id.fabItem1);
        fabItem1.setOnClickListener(new View.OnClickListener() {
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
        fabItem1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createToast("Open Community");
                return true;
            }
        });

        // Load Shake Service if off
        if (!isServiceRunning(ListenerService.class)) {
            Intent i = new Intent(this, ListenerService.class);
            this.startService(i);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

}
