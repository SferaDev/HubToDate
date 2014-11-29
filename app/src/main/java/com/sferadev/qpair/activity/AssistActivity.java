package com.sferadev.qpair.activity;

import android.app.Activity;
import android.os.Bundle;

import static com.sferadev.qpair.utils.Utils.createAssistDialog;

// AssistActivity that receives the AssistIntent
public class AssistActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch the Assist Dialog
        createAssistDialog();

        // End this dummy Activity
        finish();
    }

}
