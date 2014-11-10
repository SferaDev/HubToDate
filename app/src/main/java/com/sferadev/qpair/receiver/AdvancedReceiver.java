package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.sferadev.qpair.utils.Utils.*;

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
            default:
                createToast(intent.getAction().toString());
        }
    }
}
