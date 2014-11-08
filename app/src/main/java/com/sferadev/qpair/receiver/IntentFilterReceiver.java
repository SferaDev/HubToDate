package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.App;

import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;
import static com.sferadev.qpair.utils.Utils.ACTION_OPEN_PLAY_STORE;
import static com.sferadev.qpair.utils.Utils.EXTRA_PACKAGE_NAME;
import static com.sferadev.qpair.utils.Utils.KEY_LAST_APP;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getPreferences;
import static com.sferadev.qpair.utils.Utils.setPreferences;

public class IntentFilterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String[] data = intent.getData().toString().split(":");

        if (data[1] != getPreferences(KEY_LAST_APP, null) && isQPairOn() && isConnected()) {
            final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
            setPreferences(KEY_LAST_APP, data[1]);
            switch (intent.getAction()) {
                case "android.intent.action.PACKAGE_ADDED":
                    App.getContext().bindService(i, new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA_PACKAGE_NAME, data[1]), 0);
                    break;
                case "android.intent.action.PACKAGE_REMOVED":
                    App.getContext().bindService(i, new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA_PACKAGE_NAME, data[1]), 0);
                    break;
                default:
                    createToast("New intent received: " + intent.getAction() + " with: " + data[1]);
                    break;
            }
        }
    }
}