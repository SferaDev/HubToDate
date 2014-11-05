package com.sferadev.qpair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sferadev.qpair.service.ShakeService;
import com.sferadev.qpair.utils.Utils;
import com.shamanland.fab.FloatingActionButton;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(AdvancedUtils.ACTION_OPEN_URL);
                i.putExtra("url", "https://sferadev.com");
                sendBroadcast(i);*/
                /*@SuppressLint("ResourceAsColor")
                Delivery myDelivery = PostOffice.newMail(MainActivity.this)
                        .setTitle("Hi")
                        .setDesign(Design.MATERIAL_LIGHT)
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(true)
                        .setShouldProperlySortButtons(true)
                        .setButtonTextColor(Dialog.BUTTON_NEGATIVE, R.color.amber_500)
                        .setButton(Dialog.BUTTON_NEGATIVE, App.getContext().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setButtonTextColor(Dialog.BUTTON_POSITIVE, R.color.pink_500)
                        .setButton(Dialog.BUTTON_POSITIVE, App.getContext().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .build();

                myDelivery.show(getFragmentManager());*/
            }
        });

        if (!Utils.isServiceRunning(ShakeService.class)) {
            Intent i = new Intent(this, ShakeService.class);
            this.startService(i);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

}
