package com.sferadev.qpair.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sferadev.qpair.R;
import com.sferadev.qpair.service.ShakeService;

import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getOwnerName;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;
import static com.sferadev.qpair.utils.Utils.openURL;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView welcomeText = (TextView) findViewById(R.id.info_text_1);
        welcomeText.setText("Welcome " + getOwnerName() + "!");

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(getString(R.string.dialog_community), getString(R.string.dialog_community_description), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openURL(getString(R.string.community_url));
                    }
                }, null);
            }
        });
        floatingActionButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                createToast(getString(R.string.toast_community));
                return true;
            }
        });

        // Load Shake Service if off
        if (!isServiceRunning(ShakeService.class)) {
            Intent i = new Intent(this, ShakeService.class);
            this.startService(i);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

}
