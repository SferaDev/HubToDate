package com.sferadev.qpair.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.sferadev.qpair.R;

import static com.sferadev.qpair.utils.Utils.createToast;

// Receiver of the Admin Privileges changes
public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        // Tell the user we've got the rights
        createToast(context.getString(R.string.admin_enabled));
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        // Tell the user we've lost the rights
        createToast(context.getString(R.string.admin_disabled));
    }

}