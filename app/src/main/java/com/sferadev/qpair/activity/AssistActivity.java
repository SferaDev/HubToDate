package com.sferadev.qpair.activity;

import android.app.Activity;
import android.os.Bundle;

import com.sferadev.qpair.R;

import static com.sferadev.qpair.utils.Utils.createAssistDialog;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.isPackageInstalled;

// AssistActivity that receives the AssistIntent
public class AssistActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isPackageInstalled(getString(R.string.qpair_package))) {
            // Launch the Assist Dialog
            createAssistDialog();
        } else {
            createToast(getString(R.string.dialog_qpair_not_installed));
        }
        // End this dummy Activity
        finish();
    }

}
