package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.App;
import com.sferadev.qpair.R;
import com.sferadev.qpair.service.ShakeService;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;
import static com.sferadev.qpair.utils.Utils.ACTION_CHANGE_IME;
import static com.sferadev.qpair.utils.Utils.ACTION_CHANGE_RINGER_MODE;
import static com.sferadev.qpair.utils.Utils.ACTION_CHANGE_WIFI;
import static com.sferadev.qpair.utils.Utils.ACTION_CREATE_DIALOG;
import static com.sferadev.qpair.utils.Utils.ACTION_OPEN_PLAY_STORE;
import static com.sferadev.qpair.utils.Utils.EXTRA_MESSAGE;
import static com.sferadev.qpair.utils.Utils.EXTRA_PACKAGE_NAME;
import static com.sferadev.qpair.utils.Utils.EXTRA_RINGER_MODE;
import static com.sferadev.qpair.utils.Utils.EXTRA_WIFI_STATE;
import static com.sferadev.qpair.utils.Utils.KEY_LAST_APP;
import static com.sferadev.qpair.utils.Utils.KEY_LAST_RINGER_MODE;
import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createExplicitFromImplicitIntent;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getPreferences;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;
import static com.sferadev.qpair.utils.Utils.setPreferences;

public class IntentFilterReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, final Intent intent) {

        if (!isServiceRunning(ShakeService.class)) {
            Intent serviceIntent = new Intent(getContext(), ShakeService.class);
            getContext().startService(serviceIntent);
        }

        if (isQPairOn() && isConnected()) {
            final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
            switch (intent.getAction()) { //TODO
                case "android.intent.action.PACKAGE_ADDED":
                    final String[] dataPackageAdded = intent.getData().toString().split(":");
                    createDialog("Install on Peer", "Do you wish to install this app on your QPair device?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dataPackageAdded[1] != getPreferences(KEY_LAST_APP, null)) {
                                setPreferences(KEY_LAST_APP, dataPackageAdded[1]);
                                getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA_PACKAGE_NAME, dataPackageAdded[1]), 0);
                            }
                        }
                    }, null);
                    break;
                case "android.intent.action.PACKAGE_REMOVED":
                    final String[] dataPackageRemoved = intent.getData().toString().split(":");
                    createDialog("Uninstall on Peer", "Do you wish to uninstall this app on your QPair device?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dataPackageRemoved[1] != getPreferences(KEY_LAST_APP, null)) {
                                setPreferences(KEY_LAST_APP, dataPackageRemoved[1]);
                                getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_OPEN_PLAY_STORE, EXTRA_PACKAGE_NAME, dataPackageRemoved[1]), 0);
                            }
                        }
                    }, null);
                    break;
                case "android.intent.action.INPUT_METHOD_CHANGED":
                    createDialog("Change IME on Peer", "Do you wish to change the Input Method on your QPair device?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CHANGE_IME), 0);
                        }
                    }, null);
                    break;
                case "android.net.wifi.WIFI_STATE_CHANGED":
                    final int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                    createDialog("Update Wifi on Peer", "Do you wish to sync the Wifi state on your QPair device?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (state) {
                                case WifiManager.WIFI_STATE_DISABLED:
                                    getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA_WIFI_STATE, "false"), 0);
                                    break;
                                case WifiManager.WIFI_STATE_ENABLED:
                                    getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA_WIFI_STATE, "true"), 0);
                                    break;
                            }
                        }
                    }, null);
                    break;
                case "android.media.RINGER_MODE_CHANGED":
                    createDialog("Update Ringer on Peer", "Do you wish to sync the volume on your QPair device?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1) != getPreferences(KEY_LAST_RINGER_MODE, -1)) {
                                setPreferences(KEY_LAST_RINGER_MODE, intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));
                                getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CHANGE_RINGER_MODE, EXTRA_RINGER_MODE, intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1)), 0);
                            }
                        }
                    }, null);
                    break;
                case "android.intent.action.BATTERY_LOW":
                    getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CREATE_DIALOG, EXTRA_MESSAGE, getContext().getString(R.string.battery_low)), 0);
                    break;
                case "android.intent.action.DEVICE_STORAGE_LOW":
                    getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CREATE_DIALOG, EXTRA_MESSAGE, getContext().getString(R.string.storage_low)), 0);
                    break;
                default:
                    createToast("New intent received: " + intent.getAction());
                    break;
            }
        }
    }
}