package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.sferadev.qpair.utils.Utils.EXTRA;
import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.openActivity;
import static com.sferadev.qpair.utils.Utils.openPlayStore;
import static com.sferadev.qpair.utils.Utils.openURL;
import static com.sferadev.qpair.utils.Utils.setBrightnessLevel;
import static com.sferadev.qpair.utils.Utils.setClipboardString;
import static com.sferadev.qpair.utils.Utils.setRingerMode;
import static com.sferadev.qpair.utils.Utils.switchIME;
import static com.sferadev.qpair.utils.Utils.switchWifi;

public class AdvancedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "com.sferadev.qpair.CHANGE_IME":
                switchIME();
                break;
            case "com.sferadev.qpair.CHANGE_WIFI":
                switchWifi(Boolean.parseBoolean(intent.getStringExtra(EXTRA)));
                break;
            case "com.sferadev.qpair.CHANGE_RINGER_MODE":
                setRingerMode(Integer.valueOf(intent.getStringExtra(EXTRA)));
                break;
            case "com.sferadev.qpair.CREATE_DIALOG":
                createDialog("HubToDate", intent.getStringExtra(EXTRA), null);
                break;
            case "com.sferadev.qpair.OPEN_ACTIVITY":
                openActivity(intent.getStringExtra(EXTRA));
                break;
            case "com.sferadev.qpair.OPEN_PLAY_STORE":
                openPlayStore(intent.getStringExtra(EXTRA));
                break;
            case "com.sferadev.qpair.OPEN_URL":
                openURL(intent.getStringExtra(EXTRA));
                break;
            case "com.sferadev.qpair.UPDATE_CLIPBOARD":
                setClipboardString(intent.getStringExtra(EXTRA));
                createToast("Added to clipboard: " + intent.getStringExtra(EXTRA));
                break;
            case "com.sferadev.qpair.UPDATE_BRIGHTNESS":
                setBrightnessLevel(Integer.parseInt(intent.getStringExtra(EXTRA)));
                createToast("Updated Brightness to: " + intent.getStringExtra(EXTRA));
                break;
            default:
                createToast(intent.getAction());
        }
    }
}
