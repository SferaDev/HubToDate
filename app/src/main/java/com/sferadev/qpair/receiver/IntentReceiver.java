package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.App;
import com.sferadev.qpair.utils.QPairUtils;
import com.sferadev.qpair.utils.Utils;

public class IntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO DEBUG
        Utils.createToast(intent.getAction());

        String[] data = intent.getData().toString().split(":");
        final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            App.getContext().bindService(i, new QPairUtils.sendBroadcastConnection(Utils.ACTION_OPEN_PLAY_STORE, Utils.EXTRA_PACKAGE_NAME, data[1]), 0);
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            App.getContext().bindService(i, new QPairUtils.sendBroadcastConnection(Utils.ACTION_OPEN_PLAY_STORE, Utils.EXTRA_PACKAGE_NAME, data[1]), 0);
        }
    }
}