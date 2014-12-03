package com.sferadev.qpair.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.sferadev.qpair.R;
import com.sferadev.qpair.receiver.AdminReceiver;

import static com.sferadev.qpair.utils.Utils.createToast;

// AdminActivity to handle the request and permission to Device Admin
public class AdminActivity extends Activity {

    static final int REQUEST_ENABLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request the Administrative Privileges
        requestAdmin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Activity Result to check whether the rights are given or not
        if (REQUEST_ENABLE == requestCode) {
            if (resultCode != Activity.RESULT_OK) {
                createToast(getResources().getString(R.string.admin_failure));
            }
            finish();
        }
    }

    private void requestAdmin() {
        // Component for our Intent
        ComponentName adminReceiver = new ComponentName(this,
                AdminReceiver.class);

        // Intent to get Admin Rights
        Intent activateDeviceAdminIntent =
                new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                        .putExtra(
                                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                adminReceiver)
                        .putExtra(
                                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                getResources().getString(R.string.admin_explanation));
        startActivityForResult(activateDeviceAdminIntent, REQUEST_ENABLE);
    }
}
