package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.service.ShakeService;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;
import static com.sferadev.qpair.utils.Utils.ACTION_CHANGE_IME;
import static com.sferadev.qpair.utils.Utils.ACTION_CHANGE_WIFI;
import static com.sferadev.qpair.utils.Utils.ACTION_OPEN_PLAY_STORE;
import static com.sferadev.qpair.utils.Utils.EXTRA_PACKAGE_NAME;
import static com.sferadev.qpair.utils.Utils.EXTRA_WIFI_STATE;
import static com.sferadev.qpair.utils.Utils.KEY_LAST_APP;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getPreferences;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;
import static com.sferadev.qpair.utils.Utils.setPreferences;

public class IntentFilterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Load Shake Service if off
        if (!isServiceRunning(ShakeService.class)) {
            Intent serviceIntent = new Intent(getContext(), ShakeService.class);
            getContext().startService(serviceIntent);
        }

        if (isQPairOn() && isConnected()) {
            final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
            switch (intent.getAction()) {
                case "android.intent.action.PACKAGE_ADDED":
                    String[] dataPackageAdded = intent.getData().toString().split(":");
                    if (dataPackageAdded[1] != getPreferences(KEY_LAST_APP, null)) {
                        setPreferences(KEY_LAST_APP, dataPackageAdded[1]);
                        getContext().bindService(i, new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA_PACKAGE_NAME, dataPackageAdded[1]), 0);
                    }
                    break;
                case "android.intent.action.PACKAGE_REMOVED":
                    String[] dataPackageRemoved = intent.getData().toString().split(":");
                    if (dataPackageRemoved[1] != getPreferences(KEY_LAST_APP, null)) {
                        setPreferences(KEY_LAST_APP, dataPackageRemoved[1]);
                        getContext().bindService(i, new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA_PACKAGE_NAME, dataPackageRemoved[1]), 0);
                    }
                    break;
                case "android.intent.action.INPUT_METHOD_CHANGED":
                    getContext().bindService(i, new sendBroadcastConnection(ACTION_CHANGE_IME), 0);
                    break;
                case "android.net.wifi.WIFI_STATE_CHANGED":
                    int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                    switch (state) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            getContext().bindService(i, new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA_WIFI_STATE, "false"), 0);
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            getContext().bindService(i, new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA_WIFI_STATE, "true"), 0);
                            break;
                    }
                    break;
                default:
                    createToast("New intent received: " + intent.getAction());
                    break;
            }
        }
    }
}