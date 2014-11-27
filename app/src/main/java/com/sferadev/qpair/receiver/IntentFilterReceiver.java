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
import static com.sferadev.qpair.utils.Utils.EXTRA;
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
            switch (intent.getAction()) {
                case "android.intent.action.INPUT_METHOD_CHANGED":
                    createDialog(getContext().getString(R.string.dialog_ime), getContext().getString(R.string.dialog_ime_description), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CHANGE_IME), 0);
                        }
                    }, null);
                    break;
                case "android.net.wifi.WIFI_STATE_CHANGED":
                    final int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                    createDialog(getContext().getString(R.string.dialog_wifi), getContext().getString(R.string.dialog_wifi_description), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (state) {
                                case WifiManager.WIFI_STATE_DISABLED:
                                    getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA, "false"), 0);
                                    break;
                                case WifiManager.WIFI_STATE_ENABLED:
                                    getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA, "true"), 0);
                                    break;
                            }
                        }
                    }, null);
                    break;
                case "android.media.RINGER_MODE_CHANGED":
                    createDialog(getContext().getString(R.string.dialog_ringer), getContext().getString(R.string.dialog_ringer_description), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1) != getPreferences(KEY_LAST_RINGER_MODE, -1)) {
                                setPreferences(KEY_LAST_RINGER_MODE, intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));
                                getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CHANGE_RINGER_MODE, EXTRA, intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1)), 0);
                            }
                        }
                    }, null);
                    break;
                case "android.intent.action.BATTERY_LOW":
                    getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CREATE_DIALOG, EXTRA, getContext().getString(R.string.dialog_battery_low)), 0);
                    break;
                case "android.intent.action.DEVICE_STORAGE_LOW":
                    getContext().bindService(createExplicitFromImplicitIntent(App.getContext(), i), new sendBroadcastConnection(ACTION_CREATE_DIALOG, EXTRA, getContext().getString(R.string.dialog_storage_low)), 0);
                    break;
                default:
                    createToast(getContext().getString(R.string.toast_intent) + intent.getAction());
                    break;
            }
        }
    }
}