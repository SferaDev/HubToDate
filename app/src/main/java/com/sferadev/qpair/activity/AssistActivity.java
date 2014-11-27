package com.sferadev.qpair.activity;

import android.app.Activity;
import android.os.Bundle;

import static com.sferadev.qpair.utils.Utils.createAssistDialog;

public class AssistActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAssistDialog();
        finish();
    }

}
