package com.sferadev.qpair.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.gesture.GestureLibrary;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.R;
import com.sferadev.qpair.listener.ShakeListener;
import com.sferadev.qpair.utils.QPairUtils;
import com.shamanland.fab.FloatingActionButton;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.Utils.ACTION_OPEN_ACTIVITY;
import static com.sferadev.qpair.utils.Utils.ACTION_UPDATE_CLIPBOARD;
import static com.sferadev.qpair.utils.Utils.EXTRA_MESSAGE;
import static com.sferadev.qpair.utils.Utils.EXTRA_PACKAGE_NAME;
import static com.sferadev.qpair.utils.Utils.createExplicitFromImplicitIntent;
import static com.sferadev.qpair.utils.Utils.createToast;
import static com.sferadev.qpair.utils.Utils.getClipboardString;
import static com.sferadev.qpair.utils.Utils.getForegroundApp;

public class ListenerService extends Service {

    private GestureLibrary gestureLib;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // ShakeDetector initialization
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ShakeListener mShakeDetector = new ShakeListener();
        mShakeDetector.setOnShakeListener(new ShakeListener.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if (count > 1) {
                    if (QPairUtils.isQPairOn() && QPairUtils.isConnected()) {
                        final Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
                        getContext().bindService(createExplicitFromImplicitIntent(getContext(), intent), new QPairUtils.sendBroadcastConnection(ACTION_OPEN_ACTIVITY, EXTRA_PACKAGE_NAME, getForegroundApp()), 0);
                    } else {
                        createToast("QPair: You're not connected to Peer");
                    }
                }
            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.empty, null);
        FloatingActionButton fab = (FloatingActionButton) myView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
                getContext().bindService(createExplicitFromImplicitIntent(getContext(), i), new QPairUtils.sendBroadcastConnection(ACTION_UPDATE_CLIPBOARD, EXTRA_MESSAGE, getClipboardString()), 0);
            }
        });

        //TODO: https://github.com/futuresimple/android-floating-action-button
        //wm.addView(myView, params);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}

