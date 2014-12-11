package com.sferadev.qpair.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sferadev.qpair.R;
import com.sferadev.qpair.egg.EggActivity;
import com.sferadev.qpair.egg.LGEggActivity;
import com.sferadev.qpair.service.ShakeService;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.QPairUtils.isPhone;
import static com.sferadev.qpair.utils.QPairUtils.isR2D2;
import static com.sferadev.qpair.utils.Utils.FLAG_FLOATING_WINDOW;
import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getOwnerName;
import static com.sferadev.qpair.utils.Utils.isPackageInstalled;
import static com.sferadev.qpair.utils.Utils.isServiceRunning;
import static com.sferadev.qpair.utils.Utils.openURL;

// MainActivity that handles the creation of main UI elements
public class MainActivity extends BaseActivity {
    int eggTaps = 0;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Get the name of the device Owner and display it on a Card Text
            if (getOwnerName() != null) {
                TextView welcomeText = (TextView) findViewById(R.id.info_text_1);
                welcomeText.setText(getString(R.string.welcome) + " " + getOwnerName() + "!");
            }
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        try {
            // Check if User is in r2 and show a nice dialog telling them to update
            if (!isR2D2()) {
                createDialog(getString(R.string.dialog_update_qpair), getString(R.string.dialog_update_qpair_description), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Handle the Easter Egg
        CardView welcomeCard = (CardView) findViewById(R.id.main_hello);
        welcomeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eggTaps > 7) {
                    eggTaps = 0;
                    if (isPackageInstalled(getString(R.string.qpair_package)) && !isPhone()) {
                        Intent intent = new Intent(getContext(), LGEggActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("com.lge.app.floating.launchAsFloating", true);
                        getContext().startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), EggActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .addFlags(FLAG_FLOATING_WINDOW);
                        getContext().startActivity(intent);
                    }
                } else {
                    eggTaps++;
                }
            }
        });

        // Handle the FAB
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
}
