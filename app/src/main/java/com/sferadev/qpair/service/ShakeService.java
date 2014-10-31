package com.sferadev.qpair.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;

import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.Utils;
import com.sferadev.qpair.listener.ShakeListener;
import com.sferadev.qpair.utils.QPairUtils;

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
                    Utils.createToast("QPair: Opening app in G Pad");
                    String packageName = foregroundTaskInfo.topActivity.getPackageName();
                    String activityName = foregroundTaskInfo.topActivity.getShortClassName();
                    final Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);

                    // Bind to the QPair service
                    boolean bindResult = bindService(intent, new QPairUtils.MyActivityConnection(packageName, activityName), 0);

                    if (!bindResult) {
                        Utils.createToast("QPair: Binding to service has failed");
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

