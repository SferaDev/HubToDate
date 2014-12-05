package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sferadev.qpair.utils.QPairUtils;
import com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.QPairUtils.getQpairIntent;
import static com.sferadev.qpair.utils.Utils.ACTION_CHANGE_WIFI;
import static com.sferadev.qpair.utils.Utils.ACTION_OPEN_ACTIVITY;
import static com.sferadev.qpair.utils.Utils.ACTION_SCREEN_OFF;
import static com.sferadev.qpair.utils.Utils.ACTION_UPDATE_BRIGHTNESS;
import static com.sferadev.qpair.utils.Utils.ACTION_UPDATE_CLIPBOARD;
import static com.sferadev.qpair.utils.Utils.ACTION_VIBRATE;
import static com.sferadev.qpair.utils.Utils.EXTRA;
import static com.sferadev.qpair.utils.Utils.createAssistDialog;
import static com.sferadev.qpair.utils.Utils.getClipboardString;
import static com.sferadev.qpair.utils.Utils.getForegroundApp;
import static com.sferadev.qpair.utils.Utils.getSystemPreference;

// Tasker Receiver
public class TaskerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getStringExtra(EXTRA)) {
            // Case: Assist Dialog
            case "ASSIST":
                createAssistDialog();
                break;
            // Case: Sync App
            case "SYNC_APP":
                getContext().bindService(getQpairIntent(),
                        new sendBroadcastConnection(ACTION_OPEN_ACTIVITY, EXTRA, getForegroundApp()), 0);
                break;
            // Case: Sync Clipboard
            case "SYNC_CLIPBOARD":
                getContext().bindService(getQpairIntent(),
                        new QPairUtils.sendBroadcastConnection(ACTION_UPDATE_CLIPBOARD, EXTRA, getClipboardString()), 0);
                break;
            // Case: Sync Brightness
            case "SYNC_BRIGHTNESS":
                getContext().bindService(getQpairIntent(),
                        new QPairUtils.sendBroadcastConnection(ACTION_UPDATE_BRIGHTNESS, EXTRA,
                                getSystemPreference(android.provider.Settings.System.SCREEN_BRIGHTNESS)), 0);
                break;
            // Case: Screen Off
            case "SCREEN_OFF":
                getContext().bindService(getQpairIntent(),
                        new QPairUtils.sendBroadcastConnection(ACTION_SCREEN_OFF, EXTRA, "screenOff"), 0);
                break;
            // Case: Wifi On
            case "WIFI_ON":
                getContext().bindService(getQpairIntent(),
                        new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA, "true"), 0);
                break;
            // Case: Wifi Off
            case "WIFI_OFF":
                getContext().bindService(getQpairIntent(),
                        new sendBroadcastConnection(ACTION_CHANGE_WIFI, EXTRA, "false"), 0);
                break;
            // Case: Wifi Off
            case "VIBRATE":
                getContext().bindService(getQpairIntent(),
                        new sendBroadcastConnection(ACTION_VIBRATE, EXTRA, "doVibrate"), 0);
                break;
            // Default Case
            default:
                break;
        }
    }
}
