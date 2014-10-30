package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sferadev.qpair.Utils;

public class IntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.createToast(intent.getAction());
        String[] data = intent.getData().toString().split(":");
        Utils.createToast(data[1]);
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            // TODO
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            // TODO
        }
    }
}