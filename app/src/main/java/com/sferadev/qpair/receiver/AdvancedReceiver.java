package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.sferadev.qpair.utils.Utils.EXTRA_PACKAGE_NAME;
import static com.sferadev.qpair.utils.Utils.EXTRA_RINGER_MODE;
import static com.sferadev.qpair.utils.Utils.EXTRA_URL;
import static com.sferadev.qpair.utils.Utils.EXTRA_WIFI_STATE;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.openActivity;
import static com.sferadev.qpair.utils.Utils.openPlayStore;
import static com.sferadev.qpair.utils.Utils.openURL;
import static com.sferadev.qpair.utils.Utils.setRingerMode;
import static com.sferadev.qpair.utils.Utils.switchIME;
import static com.sferadev.qpair.utils.Utils.switchWifi;

public class AdvancedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case "com.sferadev.qpair.OPEN_ACTIVITY":
                openActivity(intent.getStringExtra(EXTRA_PACKAGE_NAME));
                break;
            case "com.sferadev.qpair.OPEN_PLAY_STORE":
                openPlayStore(intent.getStringExtra(EXTRA_PACKAGE_NAME));
                break;
            case "com.sferadev.qpair.OPEN_URL":
                openURL(intent.getStringExtra(EXTRA_URL));
                break;
            case "com.sferadev.qpair.CHANGE_IME":
                switchIME();
                break;
            case "com.sferadev.qpair.CHANGE_WIFI":
                switchWifi(Boolean.parseBoolean(intent.getStringExtra(EXTRA_WIFI_STATE)));
                break;
            case "com.sferadev.qpair.CHANGE_RINGER_MODE":
                setRingerMode(Integer.valueOf(intent.getStringExtra(EXTRA_RINGER_MODE)));
                break;
            default:
                createToast(intent.getAction().toString());
        }
    }
}
