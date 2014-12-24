package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;

import com.sferadev.qpair.R;
import com.sferadev.qpair.service.ShakeService;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.Constants.ACTION_CHANGE_IME;
import static com.sferadev.qpair.utils.Constants.ACTION_CHANGE_RINGER_MODE;
import static com.sferadev.qpair.utils.Constants.ACTION_CHANGE_WIFI;
import static com.sferadev.qpair.utils.Constants.ACTION_CREATE_DIALOG;
import static com.sferadev.qpair.utils.Constants.EXTRA;
import static com.sferadev.qpair.utils.Constants.KEY_ALWAYS_RINGER;
import static com.sferadev.qpair.utils.Constants.KEY_ALWAYS_WIFI;
import static com.sferadev.qpair.utils.Constants.KEY_LAST_RINGER_MODE;
import static com.sferadev.qpair.utils.Constants.KEY_SYNC_VOLUME;
import static com.sferadev.qpair.utils.Constants.KEY_SYNC_WIFI;
import static com.sferadev.qpair.utils.PreferenceUtils.getPreference;
import static com.sferadev.qpair.utils.PreferenceUtils.setPreference;
import static com.sferadev.qpair.utils.QPairUtils.getQpairIntent;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;
import static com.sferadev.qpair.utils.UIUtils.createDialog;
import static com.sferadev.qpair.utils.UIUtils.createToast;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;

// Receiver of System Broadcasts that handle the forward to Peer
public class IntentFilterReceiver extends BroadcastReceiver {
    // Action to perform if Battery is low
    private static void doBatteryAction() {
        getContext().bindService(getQpairIntent(),
                new sendBroadcastConnection(ACTION_CREATE_DIALOG, EXTRA, getContext().getString(R.string.dialog_battery_low)), 0);
    }

    // Action to perform if there's an InputMethod change
    private static void doIMEAction() {
        getContext().bindService(getQpairIntent(),
                new sendBroadcastConnection(ACTION_CHANGE_IME), 0);
    }

    // Action to perform if there's a change with Ringer Volume
    private static void doRingerAction(Intent intent) {
        if (intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1) != getPreference(KEY_LAST_RINGER_MODE, -1)) {
            setPreference(KEY_LAST_RINGER_MODE, intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));
            getContext().bindService(getQpairIntent(),
                    new sendBroadcastConnection(ACTION_CHANGE_RINGER_MODE, EXTRA,
                            intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1)), 0);
        }
    }

    // Action to perform if Storage is low
    private static void doStorageAction() {
        getContext().bindService(getQpairIntent(),
                new sendBroadcastConnection(ACTION_CREATE_DIALOG, EXTRA, getContext().getString(R.string.dialog_storage_low)), 0);
    }

    // Action to perform if there's a change on WiFi state
    private static void doWifiAction(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_DISABLED:
                getContext().bindService(getQpairIntent(),
                        new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA, "false"), 0);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                getContext().bindService(getQpairIntent(),
                        new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA, "true"), 0);
                break;
        }
    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        // Ensure our service is up and running
        if (!isServiceRunning(ShakeService.class)) {
            Intent serviceIntent = new Intent(getContext(), ShakeService.class);
            getContext().startService(serviceIntent);
        }

        // If we are connected forward the Broadcasts
        if (isQPairOn() && isConnected()) {
            switch (intent.getAction()) {
                case "android.intent.action.BATTERY_LOW":
                    doBatteryAction();
                    break;
                case "android.intent.action.DEVICE_STORAGE_LOW":
                    doStorageAction();
                    break;
                case "android.intent.action.INPUT_METHOD_CHANGED":
                    createDialog(getContext().getString(R.string.dialog_ime),
                            getContext().getString(R.string.dialog_ime_description), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    doIMEAction();
                                }
                            }, null);
                    break;
                case "android.media.RINGER_MODE_CHANGED":
                    if (!getPreference(KEY_SYNC_VOLUME, true)) break;
                    if (!getPreference(KEY_ALWAYS_RINGER, false)) {
                        createDialog(getContext().getString(R.string.dialog_ringer),
                                getContext().getString(R.string.dialog_ringer_description), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        doRingerAction(intent);
                                    }
                                }, null, new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setPreference(KEY_ALWAYS_RINGER, true);
                                        doRingerAction(intent);
                                    }
                                });
                    } else {
                        doRingerAction(intent);
                    }
                    break;
                case "android.net.wifi.WIFI_STATE_CHANGED":
                    if (!getPreference(KEY_SYNC_WIFI, true)) break;
                    final int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                    if (!getPreference(KEY_ALWAYS_WIFI, false)) {
                        createDialog(getContext().getString(R.string.dialog_wifi),
                                getContext().getString(R.string.dialog_wifi_description), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        doWifiAction(state);
                                    }
                                }, null, new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setPreference(KEY_ALWAYS_WIFI, true);
                                        doWifiAction(state);
                                    }
                                });
                    } else {
                        doWifiAction(state);
                    }
                    break;
                // Default case, this should never happen.
                default:
                    createToast(getContext().getString(R.string.toast_intent) + " " + intent.getAction());
                    break;
            }
        }
    }
}