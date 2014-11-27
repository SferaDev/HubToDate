package com.sferadev.qpair.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.sferadev.qpair.R;

import static com.sferadev.qpair.utils.Utils.createToast;

public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        createToast(context.getString(R.string.admin_enabled));
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        createToast(context.getString(R.string.admin_disabled));
    }

}