package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.App;
import com.sferadev.qpair.service.ShakeService;

import static com.sferadev.qpair.utils.QPairUtils.*;
import static com.sferadev.qpair.utils.Utils.*;

public class IntentFilterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Load Shake Service if off
        if (!isServiceRunning(ShakeService.class)) {
            Intent serviceIntent = new Intent(App.getContext(), ShakeService.class);
            App.getContext().startService(serviceIntent);
        }

        if (isQPairOn() && isConnected()) {
            final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
            switch (intent.getAction()) {
                case "android.intent.action.PACKAGE_ADDED":
                    String[] dataPackageAdded = intent.getData().toString().split(":");
                    if (dataPackageAdded[1] != getPreferences(KEY_LAST_APP, null)) {
                        setPreferences(KEY_LAST_APP, dataPackageAdded[1]);
                        App.getContext().bindService(i, new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA_PACKAGE_NAME, dataPackageAdded[1]), 0);
                    }
                    break;
                case "android.intent.action.PACKAGE_REMOVED":
                    String[] dataPackageRemoved = intent.getData().toString().split(":");
                    if (dataPackageRemoved[1] != getPreferences(KEY_LAST_APP, null)) {
                        setPreferences(KEY_LAST_APP, dataPackageRemoved[1]);
                        App.getContext().bindService(i, new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA_PACKAGE_NAME, dataPackageRemoved[1]), 0);
                    }
                    break;
                case "android.intent.action.INPUT_METHOD_CHANGED":
                    App.getContext().bindService(i, new sendBroadcastConnection(ACTION_CHANGE_IME, null, null), 0);
                    break;
                default:
                    createToast("New intent received: " + intent.getAction());
                    break;
            }
        }
    }
}