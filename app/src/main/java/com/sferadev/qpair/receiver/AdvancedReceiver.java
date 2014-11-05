package com.sferadev.qpair.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sferadev.qpair.utils.AdvancedUtils;
import com.sferadev.qpair.utils.Utils;

public class AdvancedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == AdvancedUtils.ACTION_OPEN_ACTIVITY) {
            Utils.openActivity(intent.getStringExtra("packageName"));
        }
        if (intent.getAction() == AdvancedUtils.ACTION_OPEN_URL) {
            Utils.openURL(intent.getStringExtra("url"));
        }
    }
}
