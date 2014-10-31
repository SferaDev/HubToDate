package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.App;
import com.sferadev.qpair.Utils;
import com.sferadev.qpair.utils.QPairUtils;

public class IntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.createToast(intent.getAction()); //TODO DEBUG
        String[] data = intent.getData().toString().split(":");
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
            boolean bindResult = App.getContext().bindService(i, new QPairUtils.MyWebsiteConnection("https://play.google.com/store/apps/details?id=" + data[1]), 0);
            if (!bindResult) {
                Utils.createToast("QPair: Binding to service has failed");
            }
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            //TODO
        }
    }
}