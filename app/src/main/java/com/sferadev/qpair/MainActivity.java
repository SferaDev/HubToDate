package com.sferadev.qpair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.service.ListenerService;
import com.sferadev.qpair.utils.QPairUtils;
import com.sferadev.qpair.utils.Utils;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createExplicitFromImplicitIntent;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getClipboardString;
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

        FloatingActionButton fabItem2 = (FloatingActionButton) findViewById(R.id.fabItem2);
        fabItem2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog("Sync Clipboard", "Sync the clipboard between your devices", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
                        getContext().bindService(createExplicitFromImplicitIntent(getContext(), i), new QPairUtils.sendBroadcastConnection(Utils.ACTION_UPDATE_CLIPBOARD, Utils.EXTRA_MESSAGE, getClipboardString()), 0);
                    }
                }, null);
            }
        });
        fabItem2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createToast("Sync Clipboard");
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
