package com.sferadev.qpair.activity;

import android.app.Activity;
import android.os.Bundle;

import com.sferadev.qpair.R;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.Utils.createAssistDialog;
import static com.sferadev.qpair.utils.Utils.openActivity;

public class AssistActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isQPairOn() && isConnected()) {
            createAssistDialog();
        } else {
            openActivity(getContext().getResources().getString(R.string.qpair_package));
        }
        finish();
    }

}
