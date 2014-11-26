package com.sferadev.qpair.service;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.listener.ShakeListener;
import com.sferadev.qpair.utils.QPairUtils;
import com.sferadev.qpair.utils.Utils;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.QPairUtils.isConnected;
import static com.sferadev.qpair.utils.QPairUtils.isQPairOn;
import static com.sferadev.qpair.utils.QPairUtils.sendBroadcastConnection;
import static com.sferadev.qpair.utils.Utils.ACTION_OPEN_ACTIVITY;
import static com.sferadev.qpair.utils.Utils.EXTRA_PACKAGE_NAME;
import static com.sferadev.qpair.utils.Utils.createDialog;
import static com.sferadev.qpair.utils.Utils.createExplicitFromImplicitIntent;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getClipboardString;
import static com.sferadev.qpair.utils.Utils.getForegroundApp;

public class ShakeService extends Service {

    String[] shakeOptions = {
            "Sync Current App",
            "Sync Clipboard",
            "Sync Brightness",
            "Turn Peer Screen Off",
            "Power off Peer",
            "Restart Peer"
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ShakeListener mShakeDetector = new ShakeListener();
        mShakeDetector.setOnShakeListener(new ShakeListener.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if (count > 1) { //TODO Simplify UX, Shake does not mean StartActivity on Peer!
                    if (isQPairOn() && isConnected()) {
                        createDialog("HubToDate", shakeOptions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        final Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
                                        getContext().bindService(createExplicitFromImplicitIntent(getContext(), intent), new sendBroadcastConnection(ACTION_OPEN_ACTIVITY, EXTRA_PACKAGE_NAME, getForegroundApp()), 0);
                                        break;
                                    case 1:
                                        final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
                                        getContext().bindService(createExplicitFromImplicitIntent(getContext(), i), new QPairUtils.sendBroadcastConnection(Utils.ACTION_UPDATE_CLIPBOARD, Utils.EXTRA_MESSAGE, getClipboardString()), 0);
                                        break;
                                    default:
                                        createToast("option is: " + which);
                                        break;
                                }
                            }
                        });

                    } else {
                        createToast("QPair: You're not connected to Peer");
                    }
                }
            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}

