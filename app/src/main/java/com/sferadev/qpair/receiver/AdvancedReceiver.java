package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.sferadev.qpair.utils.Utils.*;

public class AdvancedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == ACTION_OPEN_ACTIVITY) {
            openActivity(intent.getStringExtra(EXTRA_PACKAGE_NAME));
        }
        if (intent.getAction() == ACTION_OPEN_PLAY_STORE) {
            openPlayStore(intent.getStringExtra(EXTRA_PACKAGE_NAME));
        }
        if (intent.getAction() == ACTION_OPEN_URL) {
            openURL(intent.getStringExtra(EXTRA_URL));
        }
    }
}
