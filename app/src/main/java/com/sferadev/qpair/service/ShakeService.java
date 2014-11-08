package com.sferadev.qpair.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.App;
import com.sferadev.qpair.listener.ShakeListener;
import com.sferadev.qpair.utils.QPairUtils;

import static com.sferadev.qpair.utils.Utils.ACTION_OPEN_ACTIVITY;
import static com.sferadev.qpair.utils.Utils.EXTRA_PACKAGE_NAME;
import static com.sferadev.qpair.utils.Utils.createToast;

public class ShakeService extends Service {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeListener mShakeDetector;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeListener();
        mShakeDetector.setOnShakeListener(new ShakeListener.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if (count > 1) {
                    ActivityManager am = (ActivityManager) ShakeService.this.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);

                    if (QPairUtils.isQPairOn() && QPairUtils.isConnected()) {
                        final Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);
                        App.getContext().bindService(intent, new QPairUtils.sendBroadcastConnection(ACTION_OPEN_ACTIVITY, EXTRA_PACKAGE_NAME, foregroundTaskInfo.topActivity.getPackageName()), 0);
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

